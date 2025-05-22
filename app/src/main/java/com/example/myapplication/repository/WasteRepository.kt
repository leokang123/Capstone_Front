package com.example.myapplication.repository

import com.example.myapplication.data.waste.MoveRequest
import com.example.myapplication.data.waste.MoveRequests
import com.example.myapplication.data.waste.SearchRequest
import com.example.myapplication.data.waste.WasteItem
import com.example.myapplication.data.waste.WasteItemDetailResponse
import com.example.myapplication.data.waste.WasteItemDetails
import com.example.myapplication.data.waste.WasteItemRequest
import com.example.myapplication.data.waste.WasteItemResponse
import com.example.myapplication.data.waste.WasteLog
import com.example.myapplication.data.waste.WasteStorage

interface WasteRepository {
    suspend fun registerWaste(wasteItem: WasteItem): Boolean
    suspend fun searchWasteItems(searchRequest: SearchRequest): List<WasteItem>?

    suspend fun getWasteItems(): List<WasteItem>?
    suspend fun getWasteItemsByAddress(beaconAddressList: List<String>): List<WasteItem>?
    suspend fun getWasteItem(itemId: String): WasteItem?

    suspend fun getStorageWasteItems(storageId: Int): List<WasteItem>?
    suspend fun getWasteItemsByName(wasteId: String): List<WasteItem>?

    suspend fun moveWasteItem(moveRequest: MoveRequest)

    suspend fun moveWasteItems(moveRequestList: List<MoveRequest>)

//    suspend fun getDetailWasteItem(itemId: String): List<WasteLog>?
    suspend fun getDetailWasteItem(itemId: String): WasteItemDetails?


    suspend fun checkItemStatus(itemId: Long): Boolean

    suspend fun updateItem(wasteItem: WasteItem): WasteItem?

    suspend fun deleteItem(itemId: String): Boolean
}