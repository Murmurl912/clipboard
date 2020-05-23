package com.example.clipboard.client.repository;

import com.example.clipboard.client.repository.entity.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppRepository extends JpaRepository<App, String> {

}
