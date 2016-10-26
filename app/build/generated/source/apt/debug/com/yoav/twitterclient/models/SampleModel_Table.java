package com.yoav.twitterclient.models;

import com.raizlabs.android.dbflow.runtime.BaseContentProvider.PropertyConverter;
import com.raizlabs.android.dbflow.sql.QueryBuilder;
import com.raizlabs.android.dbflow.sql.language.property.BaseProperty;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;
import com.raizlabs.android.dbflow.sql.language.property.Property;
import java.lang.IllegalArgumentException;
import java.lang.Long;
import java.lang.String;

/**
 * This is generated code. Please do not modify */
public final class SampleModel_Table {
  public static final PropertyConverter PROPERTY_CONVERTER = new PropertyConverter(){ 
  public IProperty fromName(String columnName) {
  return com.yoav.twitterclient.models.SampleModel_Table.getProperty(columnName); 
  }
  };

  public static final Property<Long> id = new Property<Long>(SampleModel.class, "id");

  public static final Property<String> name = new Property<String>(SampleModel.class, "name");

  public static final IProperty[] getAllColumnProperties() {
    return new IProperty[]{id,name};
  }

  public static BaseProperty getProperty(String columnName) {
    columnName = QueryBuilder.quoteIfNeeded(columnName);
    switch (columnName)  {
      case "`id`":  {
        return id;
      }
      case "`name`":  {
        return name;
      }
      default:  {
        throw new IllegalArgumentException("Invalid column name passed. Ensure you are calling the correct table's column");
      }
    }
  }
}
