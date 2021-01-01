package org.dimensinfin.eveonline.neocom.database.core;

public interface ISDEStatement {
	boolean isFirst();

	boolean isLast();

	void close();

	double getDouble( final int i );

	float getFloat( final int i );

	int getInt( final int i );

	long getLong( final int i );

	short getShort( final int i );

	String getString( final int i );

	boolean moveToFirst();

	boolean moveToLast();

	boolean moveToNext();
}
