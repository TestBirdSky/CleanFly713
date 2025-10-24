package com.kiza.opkl

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.solar.tabor.AppICenter

/**
 * Date：2025/10/24
 * Describe:
 */
class RavenPlayInfo : ContentProvider() {
    override fun delete(
        p0: Uri, p1: String?, p2: Array<out String?>?
    ): Int {
        return 0
    }

    override fun getType(p0: Uri): String? {
        return ""
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return null
    }

    override fun onCreate(): Boolean {
        context?.let {
            AppICenter().init(it)
        }
        return false
    }

    override fun query(
        p0: Uri, p1: Array<out String?>?, p2: String?, p3: Array<out String?>?, p4: String?
    ): Cursor? {
        return null
    }

    override fun update(
        p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String?>?
    ): Int {
        return 0
    }

}