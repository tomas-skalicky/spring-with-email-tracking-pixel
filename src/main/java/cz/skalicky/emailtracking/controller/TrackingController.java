package cz.skalicky.emailtracking.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.skalicky.emailtracking.businesslogic.MailSenderBean;

/**
 * @author Tomas Skalicky
 *         <p>
 *         Created on 21.04.2015
 */
@Controller
@RequestMapping("")
public class TrackingController {

    private final Logger log = LogManager.getLogger(TrackingController.class);

    private final AtomicInteger sentCounter = new AtomicInteger(0);

    private final ConcurrentMap<Integer, AtomicLong> openedCounters = new ConcurrentHashMap<>();

    @Inject
    private MailSenderBean mailSender;

    @RequestMapping(value = "/sendEmail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void sendEmail() {

        final int sentNumber = sentCounter.incrementAndGet();
        openedCounters.put(sentNumber, new AtomicLong(0));

        final String message = "Email number #" + sentNumber + "\n"
                + "<img src=\"http://localhost:9090/spring-email-tracking/trackEmail/1\" />";

        mailSender.sendMail("spring-with-email-tracking-email #" + sentNumber, message);

        log.info("Email number #" + sentNumber + " has been sent");
    }

    @RequestMapping(value = "/trackEmail/{sentNumber}", method = RequestMethod.GET, produces = MediaType.IMAGE_GIF_VALUE)
    public ResponseEntity<byte[]> trackEmail(@PathVariable("sentNumber") final int sentNumber) {

        final AtomicLong openedCounter = openedCounters.get(sentNumber);
        final long openedNumber = openedCounter.incrementAndGet();

        log.info("Email number #" + sentNumber + " has been opened already " + openedNumber + " times");

        URL imageUrl;
        try {
            imageUrl = new URL("http://www.textmodegames.com/images/onepixel.gif");
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }

        byte[] imageBytes;
        try (InputStream in = imageUrl.openStream()) {
            imageBytes = IOUtils.toByteArray(in);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_GIF);

        return new ResponseEntity<byte[]>(imageBytes, headers, HttpStatus.OK);
    }

}
