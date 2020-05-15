package com.example.clipboard.client.repository;

import com.example.clipboard.client.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalAccountRepository extends CrudRepository<Account, String> {

}
