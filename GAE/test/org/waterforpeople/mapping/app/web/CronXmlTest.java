package org.waterforpeople.mapping.app.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Guards the one property of cron.xml that fails silently and expensively.
 *
 * App Engine retries a failing cron job until its next scheduled run unless the
 * entry bounds it. A job that cannot succeed at all -- one exhausting the
 * instance's memory, say -- therefore keeps asking: roughly a thousand attempts
 * a day were observed on instances serving two thousand requests in total, each
 * attempt occupying an instance that user requests were queueing for.
 *
 * Nothing reports that. The job simply never runs, and the cost lands on
 * whichever instance is unlucky enough to hold the data that provokes it. So
 * the rule is asserted here rather than left to review: cron.xml has no
 * file-level default for retry-parameters, which means every new entry is one
 * more chance to forget.
 */
public class CronXmlTest {

    // Resolved from the module root: surefire runs with GAE/ as the working
    // directory, and this file ships in the war rather than on the classpath.
    private static final String CRON_XML = "war/WEB-INF/cron.xml";

    private List<Element> cronEntries() throws Exception {
        File file = new File(CRON_XML);
        assertTrue(file.isFile(), CRON_XML + " not found; working directory is "
                + new File(".").getAbsolutePath());

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(file);

        NodeList nodes = doc.getElementsByTagName("cron");
        List<Element> entries = new ArrayList<Element>();
        for (int i = 0; i < nodes.getLength(); i++) {
            entries.add((Element) nodes.item(i));
        }
        return entries;
    }

    private static String urlOf(Element cron) {
        NodeList url = cron.getElementsByTagName("url");
        return url.getLength() == 0 ? "(no <url>)" : url.item(0).getTextContent().trim();
    }

    @Test
    public void everyJobBoundsItsRetries() throws Exception {
        List<Element> entries = cronEntries();
        assertTrue(entries.size() > 0, "cron.xml declares no jobs");

        for (Element cron : entries) {
            NodeList limits = cron.getElementsByTagName("job-retry-limit");
            assertEquals(1, limits.getLength(),
                    urlOf(cron) + " must declare exactly one <job-retry-limit>,"
                            + " or App Engine retries it until its next scheduled run");

            // A limit that parses but permits unbounded work is the same bug
            // wearing the right tag, so check the value, not just the element.
            int limit = Integer.parseInt(limits.item(0).getTextContent().trim());
            assertTrue(limit >= 0 && limit <= 5,
                    urlOf(cron) + " has a nonsensical retry limit: " + limit);
        }
    }
}
