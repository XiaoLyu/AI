import java.util.*;

public class Grid {
	private HashMap<String, String> values = new HashMap<String, String>();
	private boolean valid = true;

	public HashMap<String, String> getValues() {
		return values;
	}

	public void setValues(HashMap<String, String> values) {
		this.values = values;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}
