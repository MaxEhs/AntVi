package utils;

/**
 * The AntVi ObjectWithPercentage class. This is a helper class used by the Ant
 * class to roll for percentages.
 * 
 * @author Max Ehringhausen
 *
 * @param <T>
 */
public class ObjectWithPercentage<T> {

	private T object;
	private double percentage;

	public ObjectWithPercentage(T object, double percentage) {
		this.object = object;
		this.percentage = percentage;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

}
