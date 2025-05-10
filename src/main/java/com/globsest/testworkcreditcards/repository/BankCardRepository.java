package com.globsest.testworkcreditcards.repository;

import com.globsest.testworkcreditcards.entity.BankCard;
import com.globsest.testworkcreditcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {
    @Modifying
    @Query("DELETE FROM BankCard b WHERE b.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE BankCard b SET b.status = :status WHERE b.user.id = :userId")
    void blockAllCardsByUser(@Param("userId") Long userId,
                             @Param("status") CardStatus status); // Добавляем параметр status
}
