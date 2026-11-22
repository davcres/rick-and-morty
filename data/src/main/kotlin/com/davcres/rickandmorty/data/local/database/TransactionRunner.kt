package com.davcres.rickandmorty.data.local.database

interface TransactionRunner {
    suspend operator fun <T> invoke(block: suspend () -> T): T
}
