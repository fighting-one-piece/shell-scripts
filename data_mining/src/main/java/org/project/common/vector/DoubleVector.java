package org.project.common.vector;

import java.util.Iterator;

import org.project.common.function.DoubleDoubleFunction;
import org.project.common.function.DoubleFunction;
import org.project.common.function.Functions;
import org.project.common.iterator.AbstractIterator;

public class DoubleVector extends AbstractVector<Double> {
	
	public DoubleVector(int size) {
		super(size);
		this.values = new Double[size];
	}
	
	public DoubleVector(Double[] values) {
		super(values.length);
		this.values = values;
	}
	
	@Override
	protected Iterator<Element<Double>> iterator() {
		return new AbstractIterator<Element<Double>>() {
			private int i = 0;
			private final int n = size();
			@Override
			protected Element<Double> computeNext() {
				if (i < n) {
					return new AbstractElement(i++);
				} else {
					return endOfData();
				}
			}
		};
	}
	
	@Override
	public Vector<Double> plus(Double value) {
		Vector<Double> newVector = createOptimizedCopy();
	    if (value == 0.0) {
	      return newVector;
	    }
	    DoubleFunction df = Functions.plus(value);
	    Iterator<Element<Double>> iterator = newVector.all().iterator();
	    while (iterator.hasNext()) {
	    	Element<Double> element = iterator.next();
	    	element.set(df.apply(element.get()));
	    }
	    return newVector;
	}
	
	public static double aggregate(Vector<Double> v1, Vector<Double> v2,
			DoubleDoubleFunction aggregator, DoubleDoubleFunction combiner) {
		if (v1.size() != v2.size()) {
			throw new RuntimeException("v1 size not equal v2 size");
		}
		Iterator<Element<Double>> v1Iter = v1.all().iterator();
		Iterator<Element<Double>> v2Iter = v2.all().iterator();
		boolean isValidResult = false;
		double aResult = 0;
		while (v1Iter.hasNext() && v2Iter.hasNext()) {
			Element<Double> v1Ele = v1Iter.next();
			Element<Double> v2Ele = v2Iter.next();
			double cResult = combiner.apply(v1Ele.get(), v2Ele.get());
			if (isValidResult) {
				aResult = aggregator.apply(aResult, cResult);
			} else {
				aResult = cResult;
				isValidResult = true;
			}
		}
		return aResult;
	}
	
}
