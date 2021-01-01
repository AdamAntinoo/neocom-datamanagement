package org.dimensinfin.eveonline.neocom.database.core;

public abstract class RawStatement {
	public abstract boolean moveToFirst();

	public abstract boolean moveToLast();

	public abstract boolean moveToNext();

	public abstract boolean isFirst();

	public abstract boolean isLast();

	public abstract String getString( final int i );

	public abstract short getShort( final int i );

	public abstract int getInt( final int i );

	public abstract long getLong( final int i );

	public abstract float getFloat( final int i );

	public abstract double getDouble( final int i );

	public abstract void close();
}
