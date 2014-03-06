/********************************************************************
 * Twitter Stream Listener
 *
 * Graeme Lyon
 * March 2014
********************************************************************/

import java.io.*;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TwitterStreamListener {

	public static void main(String[] args) throws TwitterException {

		// Configure OAuth
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey("75Cmd0yO2EMkegCI0gg");
		cb.setOAuthConsumerSecret("LfSdQDS4pf1VlPhb9SDKKfTM45aoGVfPFMVf8YWTjEc");
		cb.setOAuthAccessToken("942038568-H2aLIkBhDyPJpsUnDZqWHbftqD4mp97mYNI0jDFE");
		cb.setOAuthAccessTokenSecret("acFgFLKwXliPP9fk5T2J4rpHSIjO3Xsa0TD1kMnGqJ8");

		TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
				.getInstance();

		StatusListener listener = new StatusListener() {

			public void onStatus(Status status) {

				// Get geo tag
				String geo = "N/A";
				if (status.getGeoLocation() != null) {
					geo = status.getGeoLocation().toString();
				}

				 // Print to console
//				 System.out.println("@" + status.getUser().getScreenName()
//				 + " : " + status.getCreatedAt() + " - "
//				 + status.getText() + ". place: " + status.getPlace()
//				 + ". geo: " + geo);

				// File write
				String curtime = getDateTime();
				String fn = "log/log_" + curtime + ".txt";
				try {
					writeStringToFile(
							fn,
							"|START|" + "@" + status.getUser().getScreenName()
									+ "|" + status.getCreatedAt() + "|"
									+ status.getText() + "|"
									+ status.getPlace() + "|" + geo + "|END|");
				} catch (IOException e) {
					System.err.println("IOException: " + e.getMessage());
					e.printStackTrace();
				}

			}

			public void onDeletionNotice(
					StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:"
						+ statusDeletionNotice.getStatusId());
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:"
						+ numberOfLimitedStatuses);
			}

			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId
						+ " upToStatusId:" + upToStatusId);
			}

			public void onException(Exception ex) {
				ex.printStackTrace();
			}

			@Override
			public void onStallWarning(StallWarning stallWarning) {
				System.out.println(stallWarning);
			}

		};

		// Filter parameters
		FilterQuery fq = new FilterQuery();
		String keywords[] = { "#indyref" };
		fq.track(keywords);

		// Start listening to stream
		twitterStream.addListener(listener);
		twitterStream.filter(fq);

	}

	public static void writeStringToFile(String filePathAndName,
			String stringToBeWritten) throws IOException {
		try {
			String filename = filePathAndName;
			boolean append = true;
			FileWriter fw = new FileWriter(filename, append);

			fw.write(stringToBeWritten);// appends the string to the file
			fw.write("\n");
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}

	}

	public static String getDateTime() {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("HH_MM__dd_MM_yyyy");
		return dateFormat.format(date);
	}

};