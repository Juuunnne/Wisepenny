package com.wisepenny.server.dto

import kotlinx.serialization.Serializable

/**
 * Money crosses the wire as a String (e.g. "1250.75"), never a JSON number/Double —
 * that keeps exact cents end-to-end and matches the DECIMAL(12,2) storage.
 */

@Serializable
data class TokenRequest(val email: String, val password: String)

@Serializable
data class TokenResponse(val accessToken: String, val expiresIn: Long)

@Serializable
data class AccountDto(
    val id: String,
    val label: String,
    val ibanMasked: String,
    val balance: String,
    val currency: String,
)

@Serializable
data class TransactionDto(
    val id: String,
    val date: String,
    val label: String,
    val amount: String,
    val category: String,
)

@Serializable
data class PageDto(
    val items: List<TransactionDto>,
    val page: Int,
    val total: Long,
)

@Serializable
data class BalanceByAccountDto(val accountId: String, val balance: String)

@Serializable
data class BalancesDto(val total: String, val byAccount: List<BalanceByAccountDto>)

@Serializable
data class TransferRequest(val fromAccountId: String, val amount: String, val goalId: String)

@Serializable
data class TransferResponse(val transferId: String, val newBalance: String)
