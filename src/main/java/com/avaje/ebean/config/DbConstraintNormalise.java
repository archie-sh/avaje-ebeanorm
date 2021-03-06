package com.avaje.ebean.config;

/**
 * Used to normalise table and column names which means stripping out
 * quoted identifier characters and any catalog or schema prefix.
 */
public class DbConstraintNormalise {

  protected final String[] quotedIdentifiers;

  protected boolean lowerCaseTables = true;

  protected boolean lowerCaseColumns = true;

  public DbConstraintNormalise() {
    this.quotedIdentifiers = new String[]{"\"", "'", "[", "]", "`"};
  }

  /**
   * Normalise the table name by trimming catalog and schema and removing any
   * quoted identifier characters (",',[,] etc).
   */
  public String normaliseTable(String tableName) {

    tableName = trimQuotes(tableName);
    int lastPeriod = tableName.lastIndexOf('.');
    if (lastPeriod > -1) {
      // trim off catalog and schema prefix
      tableName = tableName.substring(lastPeriod + 1);
    }
    if (lowerCaseTables) {
      tableName = tableName.toLowerCase();
    }
    return tableName;
  }

  /**
   * Normalise the column name by removing any quoted identifier characters.
   */
  public String normaliseColumn(String columnName) {

    columnName = trimQuotes(columnName);
    if (lowerCaseColumns) {
      columnName = columnName.toLowerCase();
    }
    return columnName;
  }

  /**
   * Trim off the platform quoted identifier quotes like [ ' and ".
   */
  public boolean notQuoted(String tableName) {

    // remove quoted identifier characters
    for (int i = 0; i < quotedIdentifiers.length; i++) {
      if (tableName.contains(quotedIdentifiers[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Trim off the platform quoted identifier quotes like [ ' and ".
   */
  public String trimQuotes(String tableName) {

    if (tableName == null) {
      return "";
    }
    // remove quoted identifier characters
    for (int i = 0; i < quotedIdentifiers.length; i++) {
      tableName = tableName.replace(quotedIdentifiers[i], "");
    }
    return tableName;
  }


}
