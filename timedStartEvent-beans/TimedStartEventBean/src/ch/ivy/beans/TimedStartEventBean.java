package ch.ivy.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.ivyteam.ivy.process.eventstart.IProcessStartEventBean;
import ch.ivyteam.ivy.process.eventstart.IProcessStartEventBeanRuntime;
import ch.ivyteam.ivy.process.extension.ui.ExtensionUiBuilder;
import ch.ivyteam.ivy.process.extension.ui.IUiFieldEditor;
import ch.ivyteam.ivy.process.extension.ui.UiEditorExtension;
import ch.ivyteam.ivy.request.RequestException;
import ch.ivyteam.ivy.service.ServiceException;

/**
 * An Eventbean which is triggered after a given date or interval
 * 
 * @version 29.09.2016 bb Axon Ivy6.3
 */
public class TimedStartEventBean implements IProcessStartEventBean {
	private static interface Property {
		String START_DAY_OF_WEEK = "day";
		String START_HOUR_OF_DAY = "hour";
		String START_MINUTE_OF_HOUR = "minute";
		String START_IMMEDIATELY = "immediately";
		String INTERVAL_IN_SECONDS = "interval";
	}

	public IProcessStartEventBeanRuntime eventRuntime = null;

	private final Properties properties = new Properties();
	private final String fDescr;
	private final String fBeanName;

	private Boolean immediate;
	private Boolean started;
	private Boolean running;
	private int interval;

	public TimedStartEventBean() {
		fBeanName = "ch.ivy.beans.TimedStarteEventBean";
		fDescr = "Version AxonIvy 6.3 2016-09-29";
	}

	@Override
	public String getDescription() {
		return fDescr;
	}

	@Override
	public String getName() {
		return fBeanName;
	}

	@Override
	public void initialize(IProcessStartEventBeanRuntime _eventRuntime, String configuration) {
		this.eventRuntime = _eventRuntime;
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(configuration.getBytes());
		try {
			properties.load(byteArrayInputStream);
		} catch (IOException ex) {
			eventRuntime.getRuntimeLogLogger().error("TimedStartEventBean initialize: IOException!");
		}
		eventRuntime.getRuntimeLogLogger().debug("TimedStartEventBean initialized!");
		immediate = isStartImmediately(properties);
		interval = getInterval(properties);
		running = false;
		started = false;
	}

	@Override
	public boolean isMoreThanOneInstanceSupported() {
		return false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void poll() {
		try {
			eventRuntime.fireProcessStartEventRequest(null, "Event after interval polled!", null);
		} catch (RequestException e) {
			eventRuntime.getRuntimeLogLogger().error("FireProcessStartEventRequest failed");
		}
		if (!started) {
			started = true;
			eventRuntime.setPollTimeInterval(interval * 1000L);
			eventRuntime.getRuntimeLogLogger().debug("Ordinary Poll Interval [in secs] set: " + interval);
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public void start(IProgressMonitor monitor) throws ServiceException {
		if (!running) {
			running = true;
			if (immediate) {
				started = true;
				try {
					eventRuntime.fireProcessStartEventRequest(null, "Event immediatly polled!", null);
					eventRuntime.setPollTimeInterval(interval * 1000L);
					eventRuntime.getRuntimeLogLogger()
							.debug("Immediately Fired and Poll Interval [in secs] set: " + interval);
				} catch (RequestException e) {
					eventRuntime.getRuntimeLogLogger().error("FireProcessStartEventRequest failed");
				}
			} else {
				int day = getStartDay(properties);
				int hour = getStartHour(properties);
				int minute = getStartMinute(properties);
				Date startDate = new Date();
				startDate.setHours(hour);
				startDate.setMinutes(minute);
				startDate.setSeconds(0);
				Date today = new Date();

				if (day == 0) {
					// first possible day
					if (today.after(startDate)) { // next day
						startDate.setDate(startDate.getDate() + 1);
					}
					eventRuntime.getRuntimeLogLogger().debug("Calculated StartDate (first possible day): " + startDate);
				} else {
					while (today.after(startDate) || startDate.getDay() != (day - 1)) {
						// a day next week
						startDate.setDate(startDate.getDate() + 1);
					}
					eventRuntime.getRuntimeLogLogger().debug("Calculated StartDate: " + startDate);
				}

				Long diff = startDate.getTime() - today.getTime();

				eventRuntime.setPollTimeInterval(diff);
				eventRuntime.getRuntimeLogLogger().debug("Start Poll Interval [in secs] set: " + diff / 1000L);
			}
		} else {
			throw new ServiceException("Event Bean " + getName() + " is already running");
		}
		eventRuntime.getRuntimeLogLogger().debug("TimedStartEventBean started!");
	}

	@Override
	public void stop(IProgressMonitor monitor) throws ServiceException {
		running = false;
		started = false;
		eventRuntime.getRuntimeLogLogger().debug("TimedStartEventBean stopped!");
	}

	static int getStartDay(Properties prop) {
		return parseInt(prop.getProperty(Property.START_DAY_OF_WEEK));
	}

	static int getStartHour(Properties prop) {
		return parseInt(prop.getProperty(Property.START_HOUR_OF_DAY));
	}

	static int getStartMinute(Properties prop) {
		return parseInt(prop.getProperty(Property.START_MINUTE_OF_HOUR));
	}

	static boolean isStartImmediately(Properties prop) {
		try {
			return Boolean.valueOf(prop.getProperty(Property.START_IMMEDIATELY)).booleanValue();
		} catch (Exception e) {
			return false;
		}
	}

	static int getInterval(Properties prop) {
		return parseInt(prop.getProperty(Property.INTERVAL_IN_SECONDS));
	}

	private static int parseInt(String value) {
		try {
			return Integer.parseInt(value.trim());
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Implement ConfigurationEditor The editor component is called from the
	 * StartEvent-inscription mask used to set configuration parameters for the
	 * event bean. Provides the configuration a string.
	 */
	public static class Editor extends UiEditorExtension {

		private IUiFieldEditor configEditor;

		@Override
		public void initUiFields(ExtensionUiBuilder ui) {

			ui.label("Configuration properties").create();
			configEditor = ui.textField().multiline().create();

			String help = """
					This start event bean fires periodically according to the specified parameters.
					
					Start
					=====
					Either set 'immediately=true' to start immediately or set when to start with the 'day', 'hour', 'minute' property.
					Day format is an integer between 0 and 7:
					 - day=0 : First possible day (today if before the given time otherwise tomorrow)
					 - day=1 : Sunday
					 - day=2 : Monday
					 - day=3 : Tuesday
					 - day=4 : Wednesday
					 - day=5 : Thursday
					 - day=6 : Friday
					 - day=7 : Saturday

					Interval
					========
					The format of the interval is an integer in seconds:
					- interval=1 : 1 Second
					- interval=60 : 1 Minute 
					- interval=3600 : 1 Hour
					- interval=86400 : 1 Day
					""";
			ui.label(help).multiline().create();
		}

		@Override
		public String getConfiguration() {
			return configEditor.getText().trim();
		}

		@Override
		public void setConfiguration(String config) {
			if (config == null || config.isBlank()) {
				config = """
						immediately=false
						day=0
						hour=0
						minute=0
						interval=0
						""";
			}
			configEditor.setText(config);
		}

		@Override
		public boolean acceptInput() {
			return true;
		}
	}

}
