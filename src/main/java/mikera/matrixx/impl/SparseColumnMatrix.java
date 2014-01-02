package mikera.matrixx.impl;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.util.ErrorMessages;

/**
 * Matrix stored as a collection of sparse column vectors
 * 
 * @author Mike
 *
 */
public class SparseColumnMatrix extends AMatrix implements ISparse {
	protected final int rowCount;	
	protected int columnCount;	
	protected AVector[] cols;

	public SparseColumnMatrix(AVector... columns) {
		this(columns,columns[0].length(),columns.length);
	}

	protected SparseColumnMatrix(AVector[] columns, int rowCount, int columnCount) {
		cols=columns;
		this.rowCount=rowCount;
		this.columnCount=columnCount;
	}

	@Override
	public int rowCount() {
		return rowCount;
	}

	@Override
	public int columnCount() {
		return columnCount;
	}
	

	@Override
	public boolean isMutable() {
		for (int i=0; i<columnCount; i++) {
			AVector v=cols[i];
			if (v.isMutable()) return true;
		}
		return false;
	}
	
	@Override
	public boolean isFullyMutable() {
		for (int i=0; i<columnCount; i++) {
			AVector v=cols[i];
			if (!v.isFullyMutable()) return false;
		}
		return true;
	}
	
	@Override
	public boolean isZero() {
		for (int i=0; i<columnCount; i++) {
			AVector v=cols[i];
			if (!v.isZero()) return false;
		}
		return true;
	}

	@Override
	public double get(int row, int column) {
		if ((column<0)||(column>=columnCount)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		return cols[column].get(row);
	}

	@Override
	public void set(int row, int column, double value) {
		if ((column<0)||(column>=columnCount)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		cols[column].set(row,value);
	}
	
	@Override
	public AVector getColumn(int i) {
		return cols[i];
	}
	
	@Override
	public void copyColumnTo(int i, double[] data, int offset) {
		cols[i].getElements(data, offset);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return cols[column].get(row);
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		cols[column].set(row,value);
	}
	
	@Override
	public long nonZeroCount() {
		int cc=columnCount;
		long result=0;
		for (int i=0; i<cc; i++) {
			result+=cols[i].nonZeroCount();
		}
		return result;
	}	
	
	@Override
	public double elementSum() {
		int cc=columnCount;
		double result=0;
		for (int i=0; i<cc; i++) {
			result+=cols[i].elementSum();
		}
		return result;
	}	

	@Override
	public double elementSquaredSum() {
		int cc=columnCount;
		double result=0;
		for (int i=0; i<cc; i++) {
			result+=cols[i].elementSquaredSum();
		}
		return result;
	}	

	@Override
	public SparseColumnMatrix exactClone() {
		SparseColumnMatrix a=new SparseColumnMatrix(cols.clone());
		for (int i=0; i<columnCount; i++) {
			cols[i]=cols[i].exactClone();
		}
		return a;
	}
	
	@Override
	public SparseRowMatrix getTranspose() {
		return new SparseRowMatrix(cols,columnCount,rowCount);
	}
	
	@Override
	public void applyOp(Op op) {
		for (int i=0; i<columnCount; i++) {
			cols[i].applyOp(op);
		}
	}

	public static AMatrix create(AVector... columns) {
		int cc=columns.length;
		int rc=columns[0].length();
		for (int i=1; i<cc; i++) {
			if (columns[i].length()!=rc) throw new IllegalArgumentException("Mismatched row count at column: "+i);
		}
		return new SparseColumnMatrix(columns.clone());
	}
	
	@Override
	public Matrix toMatrixTranspose() {
		Matrix m=Matrix.create(columnCount, rowCount);
		for (int i=0; i<columnCount; i++) {
			cols[i].getElements(m.data, rowCount*i);
		}
		return m;
	}

}