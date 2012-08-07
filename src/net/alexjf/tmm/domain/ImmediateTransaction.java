package net.alexjf.tmm.domain;

import java.math.BigDecimal;

import java.util.Date;

import android.content.ContentValues;

import android.database.Cursor;

import net.alexjf.tmm.exceptions.DbObjectLoadException;
import net.alexjf.tmm.exceptions.DbObjectSaveException;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * This class represents an immediate transaction of the application.
 */
public class ImmediateTransaction extends Transaction {
    private static final long serialVersionUID = 1;

    // Database tables
    public static final String TABLE_NAME = "ImmediateTransactions";

    // Table Columns
    public static final String COL_ID = "id";
    public static final String COL_EXECUTIONDATE = "executionDate";

    private Date executionDate;

    public static void onDatabaseCreation(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + 
                    "REFERENCES " + Transaction.TABLE_NAME + "," +
                COL_EXECUTIONDATE + " DATETIME " + 
                    "DEFAULT (DATETIME('now', 'localtime'))" +
            ");");
    }

    public static void onDatabaseUpgrade(SQLiteDatabase db, int oldVersion, 
                                        int newVersion) {
    }

    public ImmediateTransaction(Long id) {
        super(id);
    }

    /**
     * Constructs a new instance.
     *
     * @param moneyNode The money node associated with this transaction.
     * @param value The value of this transaction.
     * @param description The description of this transaction.
     * @param categoryId The categoryId of this transaction.
     * @param executionDate The executionDate for this instance.
     */
    public ImmediateTransaction(MoneyNode moneyNode, BigDecimal value, 
            String description, Long categoryId, Date executionDate) {
        super(moneyNode, value, description, categoryId);
        this.executionDate = executionDate;
    }

    @Override
    protected void internalLoad() throws DbObjectLoadException {
        Cursor cursor = getDb().query(TABLE_NAME, null, COL_ID + " = ?", 
                new String[] {getId().toString()}, null, null, null, null);
        
        if (cursor.moveToFirst()) {
            executionDate = new Date(cursor.getLong(1));
        } else {
            throw new DbObjectLoadException("Couldn't find immediate transaction " +
                    "associated with id "+ getId());
        }

        super.internalLoad();
    }

    @Override
    protected long internalSave() throws DbObjectSaveException {
        SQLiteDatabase db = getDb();
        try {
            db.beginTransaction();
            setId(super.internalSave());

            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_ID, getId());
            contentValues.put(COL_EXECUTIONDATE, executionDate.getTime());

            long result = db.insertWithOnConflict(TABLE_NAME, null, 
                    contentValues, SQLiteDatabase.CONFLICT_REPLACE);

            if (result > 0) {
                db.setTransactionSuccessful();
                return getId();
            } else {
                throw new DbObjectSaveException("Couldn't save immediate " +
                        "transaction data associated with id " + getId());
            }
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Gets the executionDate for this instance.
     *
     * @return The executionDate.
     */
    public Date getExecutionDate() {
        return this.executionDate;
    }

    /**
     * Sets the executionDate for this instance.
     *
     * @param executionDate The executionDate.
     */
    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
        setChanged(true);
    }
}
