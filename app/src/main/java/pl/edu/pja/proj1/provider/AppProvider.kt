package pl.edu.pja.proj1.provider

import android.content.*
import android.database.Cursor
import android.net.Uri
import androidx.annotation.Nullable
import pl.edu.pja.proj1.database.AppDatabase
import pl.edu.pja.proj1.database.dao.ExpansesDao
import pl.edu.pja.proj1.model.dto.ExpanseDto


//https://medium.com/@aniket93shetty/content-provider-for-sharing-room-database-using-kotlin-c196ca1d8471
//https://github.com/android/architecture-components-samples/blob/8f4936b34ec84f7f058fba9732b8692e97c65d8f/PersistenceContentProviderSample/app/src/main/java/com/example/android/contentprovidersample/data/CheeseDao.java#L64
private const val DB_NAME = "expanse"

class AppProvider : ContentProvider() {


    companion object {
        const val AUTHORITY = "pl.edu.pja.proj1.provider"

        val URI_EXPANSE = Uri.parse(
                "content://$AUTHORITY/$DB_NAME"
        )

        private const val CODE_EXPANSE_DIR = 1

        private const val CODE_EXPANSE_ITEM = 2

        /** The URI matcher.  */
        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH)

        init {
            MATCHER.addURI(
                    AUTHORITY,
                    DB_NAME,
                    CODE_EXPANSE_DIR
            )
            MATCHER.addURI(
                    AUTHORITY,
                    "$DB_NAME/*",
                    CODE_EXPANSE_ITEM
            )
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    @Nullable
    override fun query(uri: Uri, @Nullable projection: Array<String?>?, @Nullable selection: String?,
                       @Nullable selectionArgs: Array<String?>?, @Nullable sortOrder: String?): Cursor? {
        val code = MATCHER.match(uri)
        return if (code == CODE_EXPANSE_DIR || code == CODE_EXPANSE_ITEM) {
            val context = context ?: return null
            val expanse: ExpansesDao = AppDatabase.open(context).expanses
            val cursor: Cursor = if (code == CODE_EXPANSE_DIR) {
                expanse.selectAll()
            } else {
                expanse.selectById(ContentUris.parseId(uri))
            }
            cursor.setNotificationUri(context.contentResolver, uri)
            cursor
        } else {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String {
        return when (MATCHER.match(uri)) {
            CODE_EXPANSE_DIR -> "vnd.android.cursor.dir/$AUTHORITY.$DB_NAME"
            CODE_EXPANSE_ITEM -> "vnd.android.cursor.item/$AUTHORITY.$DB_NAME"
            else -> throw java.lang.IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return when (MATCHER.match(uri)) {
            CODE_EXPANSE_DIR -> {
                val context = context ?: return null
                val id: Long = AppDatabase.open(context).expanses
                        .insert(ExpanseDto.fromContentValues(values))
                context.contentResolver.notifyChange(uri, null)
                ContentUris.withAppendedId(uri, id)
            }
            CODE_EXPANSE_ITEM -> throw java.lang.IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw java.lang.IllegalArgumentException("Unknown URI: $uri")
        }
    }


    override fun delete(uri: Uri, selection: String?,
                        selectionArgs: Array<String?>?): Int {
        return when (MATCHER.match(uri)) {
            CODE_EXPANSE_DIR -> throw java.lang.IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            CODE_EXPANSE_ITEM -> {
                val context = context ?: return 0
                val count: Int = AppDatabase.open(context).expanses
                        .removeById(ContentUris.parseId(uri))
                context.contentResolver.notifyChange(uri, null)
                count
            }
            else -> throw java.lang.IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String?>?): Int {
        return when (MATCHER.match(uri)) {
            CODE_EXPANSE_DIR -> throw java.lang.IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            CODE_EXPANSE_ITEM -> {
                val context = context ?: return 0
                val expanse: ExpanseDto = ExpanseDto.fromContentValues(values)
                expanse.id = ContentUris.parseId(uri)
                val count: Int = AppDatabase.open(context).expanses
                        .update(expanse)
                context.contentResolver.notifyChange(uri, null)
                count
            }
            else -> throw java.lang.IllegalArgumentException("Unknown URI: $uri")
        }
    }


}