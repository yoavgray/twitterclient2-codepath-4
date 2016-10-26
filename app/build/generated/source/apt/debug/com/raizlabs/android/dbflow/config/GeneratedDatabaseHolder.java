package com.raizlabs.android.dbflow.config;

import com.raizlabs.android.dbflow.converter.BooleanConverter;
import com.raizlabs.android.dbflow.converter.CalendarConverter;
import com.raizlabs.android.dbflow.converter.DateConverter;
import com.raizlabs.android.dbflow.converter.SqlDateConverter;
import java.lang.Boolean;
import java.util.Calendar;
import java.util.Date;

public final class GeneratedDatabaseHolder extends DatabaseHolder {
  public GeneratedDatabaseHolder() {
    typeConverters.put(Calendar.class, new CalendarConverter());
    typeConverters.put(Date.class, new DateConverter());
    typeConverters.put(Boolean.class, new BooleanConverter());
    typeConverters.put(java.sql.Date.class, new SqlDateConverter());
    new MyDatabaseRestClientDatabase_Database(this);
  }
}
