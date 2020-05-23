package com.example.clipboard.server.service.reactive;

import com.example.clipboard.server.entity.Account;
import com.example.clipboard.server.entity.temp.AccessToken;
import com.example.clipboard.server.entity.temp.AccessTokenControl;
import com.example.clipboard.server.entity.temp.AccessTokenSignKey;
import com.example.clipboard.server.exception.AccountDeactivatedException;
import com.example.clipboard.server.exception.AuthenticationException;
import com.example.clipboard.server.exception.UserNotFoundException;
import com.example.clipboard.server.model.AuthenticationModel;
import com.example.clipboard.server.repository.AccessTokenControlRepository;
import com.example.clipboard.server.repository.AccessTokenRepository;
import com.example.clipboard.server.repository.AccessTokenSignKeyRepository;
import com.example.clipboard.server.repository.AccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.Binary;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Map;


@Service
public class AuthenticationReactiveService {

    private final AccountRepository accountRepository;
    private final AccessTokenSignKeyRepository signKeyRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final AccessTokenControlRepository accessTokenControlRepository;

    public static final String KEY_ALGORITHM = "RSA";
    public static final int KEY_SIZE = 2048;
    public static final String SIGN_ALGORITHM = "SHA256withRSA";

    public AuthenticationReactiveService(AccessTokenControlRepository accessTokenControlRepository,
                                         AccountRepository accountRepository,
                                         AccessTokenSignKeyRepository signKeyRepository,
                                         AccessTokenRepository accessTokenRepository) {
        this.accessTokenControlRepository = accessTokenControlRepository;
        this.accountRepository = accountRepository;
        this.signKeyRepository = signKeyRepository;
        this.accessTokenRepository = accessTokenRepository;

    }

    @PostConstruct
    private void init() {
        signKeyRepository.findFirstByStatusEquals(AccessTokenSignKey
                .AccessTokenSignKeyStatus.ACCESS_TOKEN_SIGN_KEY_STATUS_MASTER.STATUS)
                .switchIfEmpty(generate()) // todo: test implementation
                .subscribe();
    }

    public Mono<AccessToken> authenticate(@NonNull Mono<AuthenticationModel> model) {
        return model
                .map(auth -> {
                    Map<String, Object> data = auth.getData();
                    return Pair.of(data.get(AuthenticationModel.username),
                            data.get(AuthenticationModel.password));
                }).flatMap(auth -> accountRepository
                        .findAccountByUsernameEquals(auth.getFirst().toString())
                        .switchIfEmpty(Mono.error(new UserNotFoundException()))
                        .handle((account, sink) -> {
                            if(account.status.equals(Account.AccountStatus.ACCOUNT_STATUS_ACTIVATE.STATUS)) {
                                if(account.password.equals(auth.getSecond())) {
                                    sink.next(account);
                                } else {
                                    sink.error(new AuthenticationException());
                                }
                            } else if (account.status.equals(Account.AccountStatus.ACCOUNT_STATUS_REGISTERED.STATUS)) {
                                sink.error(new AccountDeactivatedException());
                            } else {
                                sink.error(new AuthenticationException());
                            }
                        }).cast(Account.class))
                .flatMap(this::auth);
    }

    public Mono<Boolean> verification(@NonNull Mono<AccessToken> accessTokenMono) {
        return accessTokenMono.flatMap(token ->
                accessTokenControlRepository.countAccessTokenControlByTokenEquals(token.id)
                .handle((count, sink) -> {
                    if(count > 0) {
                        sink.error(new AuthenticationException());
                    } else {
                        sink.next(token);
                    }
                }).cast(AccessToken.class))
                .flatMap(token -> {
                    if(token.expire.before(new Date())) {
                        return Mono.error(new AuthenticationException());
                    }

                    return signKeyRepository.findById(token.key)
                            .switchIfEmpty(Mono.error(new AuthenticationException()))
                            .map(key -> {
                                try {
                                    return recover(key);  // todo: handle block call
                                } catch (Exception e) {
                                    throw new RuntimeException(); // todo: handel exception
                                }
                            })
                            .map(KeyPair::getPublic)
                            .map(key -> {
                                try {
                                    return verify(token, key); // todo: handel block call
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                    // todo: exception handle
                                }
                            });
                });
    }

    @NonNull
    private Mono<AccessTokenSignKey> generate() {
        return Mono.fromCallable(this::generateKey)
                .subscribeOn(Schedulers.boundedElastic())
                .map(keyPair -> {
                    AccessTokenSignKey signKey = new AccessTokenSignKey();
                    signKey.privateKey = new Binary(keyPair.getPrivate().getEncoded());
                    signKey.privateKyeFormat = keyPair.getPrivate().getFormat();
                    signKey.publicKey = new Binary(keyPair.getPublic().getEncoded());
                    signKey.publicKeyFormat = keyPair.getPublic().getFormat();
                    signKey.status = AccessTokenSignKey.AccessTokenSignKeyStatus
                            .ACCESS_TOKEN_SIGN_KEY_STATUS_MASTER.STATUS;
                    return signKey;
                })
                .flatMap(signKeyRepository::insert);
    }

    @NonNull
    private Mono<AccessToken> auth(Account account) {
        return signKeyRepository.findFirstByStatusEquals(AccessTokenSignKey
                .AccessTokenSignKeyStatus.ACCESS_TOKEN_SIGN_KEY_STATUS_MASTER.STATUS)
                .switchIfEmpty(Mono.error(new AuthenticationException()))
                .flatMap(signKey -> {
                    try {
                        KeyPair keyPair = recover(signKey);
                        AccessToken accessToken = new AccessToken();
                        accessToken.account = account.id;
                        accessToken.create = new Date();
                        accessToken.expire = new Date(System.currentTimeMillis() + 3600 * 24 * 1000 * 14);
                        accessToken.key = signKey.id;
                        accessToken.salt = null;
                        return accessTokenRepository
                                .insert(accessToken)
                                .switchIfEmpty(Mono.error(new AuthenticationException()))
                                .map(token -> {
                                    try {
                                        sign(token, keyPair.getPrivate()); // todo: handle block call
                                        return token;
                                    } catch (Exception e) {
                                        throw new RuntimeException(e); // todo: handle error
                                    }
                        });
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }

    public Mono<Void> expire(@NonNull Mono<String> token) {
        return token.flatMap(id ->
                accessTokenControlRepository
                        .countAccessTokenControlByTokenEquals(id)
                        .map(count -> {
                            if(count > 0) {
                                return Mono.empty();
                            } else {
                                AccessTokenControl control = new AccessTokenControl();
                                control.token = id;
                                return accessTokenControlRepository.insert(control);
                            }
                        }))
                .then();
    }

    @NonNull
    private KeyPair recover(@NonNull AccessTokenSignKey signKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(signKey.publicKey.getData());
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(signKey.privateKey.getData());
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        return new KeyPair(publicKey, privateKey);
    }

    @NonNull
    private KeyPair generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(KEY_SIZE);
        return kpg.generateKeyPair();
    }

    @NonNull
    private byte[] sign(@NonNull AccessToken token, @NonNull PrivateKey privateKey) throws JsonProcessingException,
            NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        token.sign = null;
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = objectMapper.writeValueAsBytes(token);
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(bytes);
        byte[] signs = signature.sign();
        token.sign = Base64Utils.encodeToString(signs);
        return signs;
    }

    private boolean verify(@NonNull AccessToken token, @NonNull PublicKey publicKey) throws NoSuchAlgorithmException,
            InvalidKeyException, JsonProcessingException, SignatureException {
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initVerify(publicKey);
        String sign = token.sign;
        token.sign = null;
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = objectMapper.writeValueAsBytes(token);
        signature.update(bytes);
        return signature.verify(Base64Utils.decodeFromString(sign));
    }
}
