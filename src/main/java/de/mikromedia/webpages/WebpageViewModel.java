package de.mikromedia.webpages;

import de.deepamehta.core.JSONEnabled;
import de.deepamehta.core.Topic;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class WebpageViewModel implements JSONEnabled {

	public Topic page;

	public WebpageViewModel(Topic pageAliasTopic) {
		this.page = pageAliasTopic.getRelatedTopic("dm4.core.composition",
		    "dm4.core.child", "dm4.core.parent", "de.mikromedia.page");
		this.page.loadChildTopics();
	}

	public String getPageTitle() {
		return page.getSimpleValue().toString();
	}

	public String getPageHtmlText() {
		return page.getChildTopics().getString("de.mikromedia.page.main_part");
	}

	public Date getPageModificationDate() {
		Object modified = page.getProperty("dm4.time.modified");
		Date modificationDate = new Date();
		modificationDate.setTime((Long) modified);
		// DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
		// df.format(modificationDate);
		return modificationDate;
	}

	public Date getPageCreationDate() {
		Object created = page.getProperty("dm4.time.created");
		Date creationDate = new Date();
		creationDate.setTime((Long) created);
		// DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
		return creationDate;
	}

	public boolean isPublished() {
		return page.getChildTopics().getBoolean("de.mikromedia.page.is_published");
	}

	public String getAuthorNames() {
		String nameOfAuthors = "";
		List<Topic> authorNames = page.getChildTopics().getTopics("de.mikromedia.page.author_name");
		Iterator<Topic> iterator = authorNames.iterator();
		while (iterator.hasNext()) {
			Topic authorName = iterator.next();
			nameOfAuthors += authorName.getSimpleValue();
			if (iterator.hasNext()) nameOfAuthors += ", ";
		}
		return nameOfAuthors;
	}
	
	public JSONObject toJSON() {
		try {
			return new JSONObject()
			    .put("title", getPageTitle())
			    .put("text", getPageHtmlText())
			    .put("date", getPageModificationDate())
				.put("author_names", getAuthorNames());
			/* .put("web_alias", webAlias)
			 .put("web_description", webDescription) **/
		} catch (JSONException ex) {
			Logger.getLogger(WebpageViewModel.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

}