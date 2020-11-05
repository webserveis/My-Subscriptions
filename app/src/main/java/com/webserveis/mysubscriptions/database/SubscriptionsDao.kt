package com.webserveis.mysubscriptions.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.webserveis.mysubscriptions.models.SubscriptionsDB


@Dao
interface SubscriptionsDao {

    @Query("SELECT COUNT(uid) FROM subscriptions_table")
    fun getSubscriptionsCount(): LiveData<Int>

    @Query("SELECT * from subscriptions_table ORDER BY name ASC")
    fun getSubscriptions(): List<SubscriptionsDB>

    @Query("SELECT * FROM subscriptions_table WHERE uid=:id")
    fun getSubscriptionById(id: String): SubscriptionsDB?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(entry: SubscriptionsDB)

    @Update
    fun update(vararg entries: SubscriptionsDB)

    @Delete
    fun delete(vararg entries: SubscriptionsDB) : Int

}