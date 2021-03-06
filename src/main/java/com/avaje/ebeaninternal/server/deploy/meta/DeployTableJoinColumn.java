package com.avaje.ebeaninternal.server.deploy.meta;

import javax.persistence.JoinColumn;

import com.avaje.ebeaninternal.server.deploy.BeanProperty;
import com.avaje.ebeaninternal.server.deploy.BeanTable;

/**
 * A join pair of local and foreign properties.
 */
public class DeployTableJoinColumn {

	/**
	 * The local database column name.
	 */
	String localDbColumn;

	/**
	 * The foreign database column name.
	 */
	String foreignDbColumn;

  final boolean insertable;

  final boolean updateable;
	
	/**
	 * Construct when automatically determining the join.
	 * <p>
	 * Assume that we want the foreign key to be insertable and updateable.
	 * </p>
	 */
	public DeployTableJoinColumn(String localDbColumn, String foreignDbColumn) {
		this(localDbColumn, foreignDbColumn, true, true);
	}
	    
	/**
	 * Construct with explicit insertable and updateable flags.
	 */
	public DeployTableJoinColumn(String localDbColumn, String foreignDbColumn, boolean insertable, boolean updateable) {
		this.localDbColumn = nullEmptyString(localDbColumn);
		this.foreignDbColumn = nullEmptyString(foreignDbColumn);
		this.insertable = insertable;
		this.updateable = updateable;
	}
    
    public DeployTableJoinColumn(boolean order, JoinColumn jc, BeanTable beanTable) {
    	this(jc.referencedColumnName(), jc.name(), jc.insertable(), jc.updatable());
    	setReferencedColumn(beanTable);
    	if (!order){
    		reverse();
    	}
    }

    private void setReferencedColumn(BeanTable beanTable){
    	if (localDbColumn == null){
    		BeanProperty[] idProperties = beanTable.getIdProperties();
    		if (idProperties.length == 1){
    			localDbColumn = idProperties[0].getDbColumn();
    		}
    	}
    }
    
    /**
     * Reverse the direction of the join.
     */
    public DeployTableJoinColumn reverse() {
    	String temp = localDbColumn;
    	localDbColumn = foreignDbColumn;
    	foreignDbColumn = temp;
    	return this;
    }

    /**
     * Helper method to null out empty strings.
     */
	private String nullEmptyString(String s){
		if ("".equals(s)){
			return null;
		}
		return s;
	}
	
	
	public DeployTableJoinColumn copy(boolean reverse) {
		// Note that the insertable and updateable are just copied 
		// which may not always be the correct thing to do
		// but will leave it like this for now
		if (reverse){
			return new DeployTableJoinColumn(foreignDbColumn, localDbColumn, insertable, updateable);
			
		} else {
			return new DeployTableJoinColumn(localDbColumn, foreignDbColumn, insertable, updateable);			
		}
	}

	public String toString() {
		return localDbColumn + " = " + foreignDbColumn;
	}

	/**
	 * Return true if this column should be insertable.
	 */
	public boolean isInsertable() {
		return insertable;
	}

	/**
	 * Return true if this column should be updateable.
	 */
	public boolean isUpdateable() {
		return updateable;
	}

	/**
	 * Return the foreign database column name.
	 */
	public String getForeignDbColumn() {
		return foreignDbColumn;
	}

	/**
	 * Return the local database column name.
	 */
	public String getLocalDbColumn() {
		return localDbColumn;
	}

}
