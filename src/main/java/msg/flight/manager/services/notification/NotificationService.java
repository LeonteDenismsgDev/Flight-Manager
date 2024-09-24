package msg.flight.manager.services.notification;

import lombok.extern.slf4j.Slf4j;
import msg.flight.manager.persistence.enums.FlightState;
import msg.flight.manager.persistence.models.flights.*;
import msg.flight.manager.persistence.repositories.FlightRepository;
import msg.flight.manager.persistence.repositories.NotificationRepository;
import msg.flight.manager.persistence.repositories.TemplateRepository;
import msg.flight.manager.services.flights.FlightsMailService;
import msg.flight.manager.services.generators.FlightsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private FlightsMailService flightsMailService;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private FlightsGenerator flightsGenerator;
    @Autowired
    private NotificationRepository notificationRepository;

    @Async
    public void sendNotification(String company, LocalDateTime startTime, LocalDateTime endTime) {
        List<DBRecurrence> intervalRecurrences = flightRepository.getDBRecurrences(company, startTime);
        Set<String> recurrenceIds = intervalRecurrences.stream()
                .map(DBRecurrence::getRecursionId)
                .collect(Collectors.toSet());
        List<DBFlight> existentRecurrenceFlights = flightRepository.getDBFlights(company, recurrenceIds);
        for (DBRecurrence recurrence : intervalRecurrences) {
            processRecurrence(recurrence, existentRecurrenceFlights, startTime, endTime);
        }
    }

    private void processRecurrence(DBRecurrence recurrence, List<DBFlight> existentRecurrenceFlights,
                                   LocalDateTime startTime, LocalDateTime endTime) {

        DBTemplate template = templateRepository.findDBTemplate(recurrence.getTemplate());
        List<DBFlight> nonexistentFlights = flightsGenerator.generateFlights(recurrence, existentRecurrenceFlights, startTime, endTime, template);

        List<DBRecordNotification> recurrenceNotifications = notificationRepository.getFlightNotifications(recurrence.getType());
        Map<Integer, String> recurrenceNumbers = constructRecurrenceNotifications(recurrenceNotifications);

        Map<String, List<DBFlight>> weeklyNotified = new HashMap<>();
        Map<String, List<DBFlight>> monthlyNotified = new HashMap<>();
        Map<String, List<DBFlight>> nonNotified = new HashMap<>();
        groupFlightsByNotificationType(nonexistentFlights, recurrenceNumbers, weeklyNotified, monthlyNotified, nonNotified);
        sendWarningNotification(nonNotified, "monthly");
        sendWarningNotification(monthlyNotified, "weekly");
        disableFlights(weeklyNotified);
    }

    private void disableFlights(Map<String, List<DBFlight>> weeklyNotified) {
        for (Map.Entry<String, List<DBFlight>> entry : weeklyNotified.entrySet()) {
            List<DBFlight> flights = entry.getValue();
            for (DBFlight flight : flights) {
                flight.setState(FlightState.ANNULLED.name());
            }
            List<String> flightsId = flightRepository.insertMultipleFlights(flights).stream().map(DBFlight::getFlightNumber).toList();
            String message = flightsMailService.sendDisabledNotification(entry.getKey(), flightsId);
            notificationRepository.saveMessageNotification(new DBMessageNotification(LocalDateTime.now(), entry.getKey(), message));
        }
    }

    private void groupFlightsByNotificationType(List<DBFlight> flights, Map<Integer, String> recurrenceNumbers,
                                                Map<String, List<DBFlight>> weeklyNotified,
                                                Map<String, List<DBFlight>> monthlyNotified,
                                                Map<String, List<DBFlight>> nonNotified) {

        for (DBFlight flight : flights) {
            String editorEmail = flight.getEditor().getContactData().get("email");
            int recurrenceNumber = flight.getRecurrenceNumber();
            String notificationType = recurrenceNumbers.getOrDefault(recurrenceNumber, "");

            switch (notificationType) {
                case "weekly":
                    weeklyNotified.computeIfAbsent(editorEmail, k -> new ArrayList<>()).add(flight);
                    break;
                case "monthly":
                    monthlyNotified.computeIfAbsent(editorEmail, k -> new ArrayList<>()).add(flight);
                    break;
                default:
                    nonNotified.computeIfAbsent(editorEmail, k -> new ArrayList<>()).add(flight);
                    break;
            }
        }
    }

    private static Map<Integer, String> constructRecurrenceNotifications(List<DBRecordNotification> recurrenceNotifications) {
        Map<Integer, String> recurrenceNumbers = new HashMap<>();

        recurrenceNotifications.forEach(record -> {
            int recurrenceNumber = record.getRecurrenceNumber();
            String currentNotification = recurrenceNumbers.getOrDefault(recurrenceNumber, "");
            String newNotification = record.getFlightNotificationType();

            if ("weekly".equals(currentNotification) && "monthly".equals(newNotification)) {
                recurrenceNumbers.put(recurrenceNumber, "monthly");
            } else if (currentNotification.isEmpty()) {
                recurrenceNumbers.put(recurrenceNumber, newNotification);
            }
        });

        return recurrenceNumbers;
    }

    public void sendWarningNotification(Map<String, List<DBFlight>> nonNotifiedFlights, String notificationState) {
        List<DBRecordNotification> recordNotifications = new ArrayList<>();
        for (String user : nonNotifiedFlights.keySet()) {
            List<DBFlight> userFlights = nonNotifiedFlights.get(user);
            for (DBFlight flight : userFlights) {
                DBRecordNotification notification = new DBRecordNotification(notificationState, LocalDateTime.now(),
                        user, flight.getRecursionId(), flight.getRecurrenceNumber());
                recordNotifications.add(notification);
            }
            String notificationMessage = flightsMailService.sendWarningNotification(user, userFlights);
            DBMessageNotification messageNotification = new DBMessageNotification(LocalDateTime.now(), user, notificationMessage);
            notificationRepository.saveMessageNotification(messageNotification);
        }
        notificationRepository.insertRecordNotifications(recordNotifications);
    }
}
