package com.example.clipboard.client.repository;

import com.example.clipboard.client.entity.Clipboard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalClipboardRepository extends CrudRepository<Clipboard, String> {

}
