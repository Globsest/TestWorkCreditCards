package com.globsest.testworkcreditcards.repository;

import com.globsest.testworkcreditcards.entity.BankCard;
import com.globsest.testworkcreditcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {
    @Modifying
    @Query("DELETE FROM BankCard b WHERE b.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE BankCard b SET b.status = :status WHERE b.user.id = :userId")
    void blockAllCardsByUser(@Param("userId") Long userId,
                             @Param("status") CardStatus status);

    @Query("SELECT b FROM BankCard b WHERE b.user.id = :userId AND " +
            "(:status IS NULL OR b.status = :status)")
    Page<BankCard> findByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") CardStatus status,
            Pageable pageable
    );

    @Query("SELECT new map(b.id as id, b.card_number_masked as cardNumber, b.balance as balance, b.status as status, b.expiration_date as expiryDate) " +
            "FROM BankCard b WHERE b.user.id = :userId")
    List<Map<String, Object>> findCardBalancesByUserId(@Param("userId") Long userId);


    @Query("SELECT SUM(b.balance) FROM BankCard b WHERE b.user.id = :userId")
    Optional<BigDecimal> calculateTotalBalanceByUserId(@Param("userId") Long userId);

    Optional<BankCard> findByIdAndUserId(Long cardId, Long userId);
    Page<BankCard> findByUserId(Long userId, Pageable pageable);
}
