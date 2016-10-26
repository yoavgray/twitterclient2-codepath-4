package com.yoav.twitterclient.models;

import android.content.ContentValues;
import android.database.Cursor;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.property.BaseProperty;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseStatement;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;

public final class SampleModel_Adapter extends ModelAdapter<SampleModel> {
  public SampleModel_Adapter(DatabaseHolder holder, DatabaseDefinition databaseDefinition) {
    super(databaseDefinition);
  }

  @Override
  public final Class<SampleModel> getModelClass() {
    return SampleModel.class;
  }

  public final String getTableName() {
    return "`SampleModel`";
  }

  @Override
  public final IProperty[] getAllColumnProperties() {
    return SampleModel_Table.getAllColumnProperties();
  }

  @Override
  public final void bindToInsertValues(ContentValues values, SampleModel model) {
    if (model.id != null) {
      values.put(SampleModel_Table.id.getCursorKey(), model.id);
    } else {
      values.putNull(SampleModel_Table.id.getCursorKey());
    }
    if (model.getName() != null) {
      values.put(SampleModel_Table.name.getCursorKey(), model.getName());
    } else {
      values.putNull(SampleModel_Table.name.getCursorKey());
    }
  }

  @Override
  public final void bindToContentValues(ContentValues values, SampleModel model) {
    bindToInsertValues(values, model);
  }

  @Override
  public final void bindToInsertStatement(DatabaseStatement statement, SampleModel model, int start) {
    if (model.id != null) {
      statement.bindLong(1 + start, model.id);
    } else {
      statement.bindNull(1 + start);
    }
    if (model.getName() != null) {
      statement.bindString(2 + start, model.getName());
    } else {
      statement.bindNull(2 + start);
    }
  }

  @Override
  public final void bindToStatement(DatabaseStatement statement, SampleModel model) {
    bindToInsertStatement(statement, model, 0);
  }

  @Override
  public final String getInsertStatementQuery() {
    return "INSERT INTO `SampleModel`(`id`,`name`) VALUES (?,?)";
  }

  @Override
  public final String getCompiledStatementQuery() {
    return "INSERT INTO `SampleModel`(`id`,`name`) VALUES (?,?)";
  }

  @Override
  public final String getCreationQuery() {
    return "CREATE TABLE IF NOT EXISTS `SampleModel`(`id` INTEGER,`name` TEXT, PRIMARY KEY(`id`)" + ");";
  }

  @Override
  public final void loadFromCursor(Cursor cursor, SampleModel model) {
    int indexid = cursor.getColumnIndex("id");
    if (indexid != -1 && !cursor.isNull(indexid)) {
      model.id = cursor.getLong(indexid);
    } else {
      model.id = null;
    }
    int indexname = cursor.getColumnIndex("name");
    if (indexname != -1 && !cursor.isNull(indexname)) {
      model.setName(cursor.getString(indexname));
    } else {
      model.setName(null);
    }
  }

  @Override
  public final boolean exists(SampleModel model, DatabaseWrapper wrapper) {
    return new Select(Method.count()).from(SampleModel.class).where(getPrimaryConditionClause(model)).count(wrapper) > 0;
  }

  @Override
  public final ConditionGroup getPrimaryConditionClause(SampleModel model) {
    ConditionGroup clause = ConditionGroup.clause();
    clause.and(SampleModel_Table.id.eq(model.id));return clause;
  }

  @Override
  public final SampleModel newInstance() {
    return new SampleModel();
  }

  @Override
  public final BaseProperty getProperty(String name) {
    return SampleModel_Table.getProperty(name);
  }
}
