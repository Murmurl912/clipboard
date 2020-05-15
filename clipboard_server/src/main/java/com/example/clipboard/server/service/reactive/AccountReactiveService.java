package com.example.clipboard.server.service.reactive;


import com.example.clipboard.server.entity.Account;
import com.example.clipboard.server.entity.temp.VerificationCode;
import com.example.clipboard.server.entity.temp.VerificationCode.VerificationCodeType;
import com.example.clipboard.server.exception.*;
import com.example.clipboard.server.model.SignUpModel;
import com.example.clipboard.server.model.UpdateModel;
import com.example.clipboard.server.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

import static com.example.clipboard.server.entity.temp.VerificationCode.VerificationCodeType.VERIFICATION_CODE_TYPE_ACTIVATION;


@Service
public class AccountReactiveService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private VerificationCodeReactiveService codeService;

    public Mono<Account> register(@NonNull Mono<SignUpModel> model) {
        return model
                .flatMap(m ->
                        accountRepository
                                .countAccountByUsernameEquals(m.username)
                                .zipWith(accountRepository.countAccountByEmailEquals(m.email))
                                .handle((counts, sink) -> {
                                    if(counts.getT1() > 0) {
                                        sink.error(new UsernameRegisteredException());
                                    } else if(counts.getT2() > 1) {
                                        sink.error(new EmailRegisteredException());
                                    } else {
                                        Account account = new Account();
                                        account.username = m.username;
                                        account.password = m.password;
                                        account.email = m.email;
                                        account.avatar = null;
                                        account.create = new Date();
                                        account.update = new Date();
                                        account.status = Account.AccountStatus.ACCOUNT_STATUS_REGISTERED.STATUS;
                                        sink.next(account);
                                    }
                                }))
                .cast(Account.class)
                .flatMap(accountRepository::insert)
                .map(account -> {
                    account.password = null;
                    account.oldEmail = null;
                    account.latestEmailChange = null;
                    return account;
                });
    }

    public Mono<VerificationCode> activateRequest(@NonNull Mono<String> username) {

        return username
                .flatMap(accountRepository::findAccountStatusByUsernameEquals)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .handle((account, sink) -> {
                    if(!Account.AccountStatus.ACCOUNT_STATUS_REGISTERED.STATUS.equals(account.status)) {
                        sink.error(new AccountActivatedException());
                    } else {
                        sink.next(account);
                    }
                })
                .cast(Account.class)
                .flatMap(account -> codeService.send(account.id, account.email,
                        VERIFICATION_CODE_TYPE_ACTIVATION)
                );
    }


    /**
     * activate account, verification should be done in authorization layer
     * @param username account username
     * @return void
     */
    public Mono<Void> activate(@NonNull Mono<String> username) {
        return username.flatMap(accountRepository::findAccountByUsernameEquals)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .handle((account, sink)-> {
                    if (!Account.AccountStatus.ACCOUNT_STATUS_REGISTERED.STATUS.equals(account.status)) {
                        sink.error(new AccountActivatedException());
                    } else {
                        account.status = Account.AccountStatus.ACCOUNT_STATUS_ACTIVATE.STATUS;
                        sink.next(account);
                    }
                })
                .cast(Account.class)
                .flatMap(accountRepository::save).then();
    }

    /**
     * update only username
     * @param model update model contain username
     * @return void
     */
    public Mono<Void> updateUsername(@NonNull Mono<UpdateModel> model) {
        return model.flatMap(updateModel -> accountRepository
                .countAccountByUsernameEquals(updateModel.username))
                .handle((count, sink)->{
                    if(count > 0) {
                        sink.error(new UsernameRegisteredException());
                    } else {
                        sink.next(model);
                    }
                })
                .cast(UpdateModel.class)
                .flatMap(m -> {
                    return accountRepository
                            .findById(m.account)
                            .map(account -> {
                                account.username = m.username;
                                account.update = new Date();
                                return account;
                            });
                }).then();
    }

    /**
     * update avatar which is encoded in base64
     * @param model update model contain avatar
     * @return void
     */
    public Mono<Void> updateAvatar(@NonNull Mono<UpdateModel> model) {
        return model.flatMap(m -> accountRepository
                .findById(m.account)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(account -> {
                    account.avatar = m.avatar;
                    account.update = new Date();
                    return account;
                })).then();
    }

    /**
     * request update email
     * @return verification code
     */
    public Mono<VerificationCode> requestUpdateEmail(@NonNull Mono<String> account) {
        return accountRepository.findById(account)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .handle((acc, sink) -> {
                    if(System.currentTimeMillis() - acc.latestEmailChange.getTime() < 24 * 3600 * 1000) {
                        sink.error(new RequestBessyException());
                    } else {
                        sink.next(acc);
                    }
                })
                .cast(Account.class)
                .flatMap(acc ->
                        codeService.send(acc.id,
                                acc.email,
                                VerificationCodeType
                                        .VERIFICATION_CODE_TYPE_EMAIL
                        ));


    }

    /**
     * update account, verification code should be check in authorization layer
     * @param model update model
     * @return void
     */
    public Mono<Void> updateEmail(@NonNull Mono<UpdateModel> model) {
        return model.flatMap(updateModel -> accountRepository.countAccountByEmailEquals(updateModel.email))
                .handle((count, sink)->{
                    if(count > 0) {
                        sink.error(new EmailRegisteredException());
                    } else {
                        sink.next(model);
                    }
                })
                .cast(UpdateModel.class)
                .flatMap(m -> accountRepository
                        .findById(m.account)
                        .switchIfEmpty(Mono.error(new UserNotFoundException()))
                        .map(account -> {
                            account.oldEmail = account.email;
                            account.email = m.email;
                            account.update = new Date();
                            account.latestEmailChange = new Date();
                            account.status = Account.AccountStatus.ACCOUNT_STATUS_REGISTERED.STATUS;
                            return account;
                        }))
                .then();
    }

    /**
     * request to update password
     * @param username username
     * @return verificaiton code id
     */
    public Mono<VerificationCode> requestUpdatePassword(@NonNull Mono<String> username) {
        return username.flatMap(accountRepository::findAccountByUsernameEquals)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(acc ->
                        codeService.send(acc.id, acc.email, VerificationCodeType.VERIFICATION_CODE_TYPE_PASSWORD
                        )
                );
    }

    /**
     * update password, verification should be done in authorization layer
     * @param model update model
     * @return void
     */
    public Mono<Void> updatePassword(@NonNull Mono<UpdateModel> model) {
        return model
                .flatMap(m -> accountRepository
                        .findById(m.username)
                        .switchIfEmpty(Mono.error(new UserNotFoundException()))
                        .map(account -> {
                            account.password = m.password;
                            account.update = new Date();
                            return account;
                        })).then();
    }


    public Mono<Account> getWithoutAvatarById(@NonNull Mono<String> id) {
        return id.flatMap(accountRepository::findById)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(account -> {
                    account.password = null;
                    account.avatar = null;
                    return account;
                });
    }

    public Mono<Account> getWithoutAvatarByUsername(@NonNull Mono<String> username) {
        return username.flatMap(accountRepository::findAccountByUsernameEquals)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(account -> {
                    account.password = null;
                    account.avatar = null;
                    return account;
                });
    }

    public Mono<Account> accountWithAvatarByUsername(@NonNull Mono<String> username) {
        return username.flatMap(accountRepository::findAccountByUsernameEquals)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(account -> {
                    account.password = null;
                    return account;
                });
    }

    public Mono<Account> accountWithAvatarById(@NonNull Mono<String> id) {
        return id.flatMap(accountRepository::findById)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(account -> {
                    account.password = null;
                    return account;
                });
    }

    public Mono<String> getAvatarById(@NonNull Mono<String> id) {
        return id.flatMap(accountRepository::findById)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(account -> account.avatar);
    }

    public Mono<String> getAvatarByUsername(@NonNull Mono<String> username) {
        return username.flatMap(accountRepository::findAccountByUsernameEquals)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(account -> account.avatar);
    }
}
