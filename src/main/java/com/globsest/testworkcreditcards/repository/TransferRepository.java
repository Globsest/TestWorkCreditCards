package com.globsest.testworkcreditcards.repository;

import com.globsest.testworkcreditcards.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
