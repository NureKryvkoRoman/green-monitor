package ua.nure.kryvko.greenmonitor.sensorState;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sensor-state")
public class SensorStateController {
    @Autowired
    private SensorStateService sensorStateService;

    @GetMapping("/sensor/{id}")
    @PreAuthorize("@authorizationService.canAccessSensor(#id, authentication)")
    public ResponseEntity<List<SensorStateResponse>> getSensorStatesBySensorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(sensorStateService.getSensorStateBySensorId(id).stream()
                    .map(SensorStateDTOMapper::toDto)
                    .toList());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/greenhouse/{id}")
    @PreAuthorize("@authorizationService.canAccessGreenhouse(#id, authentication)")
    public ResponseEntity<List<SensorStateResponse>> getSensorStatesByGreenhouseId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(sensorStateService.getSensorStateByGreenhouseId(id).stream()
                    .map(SensorStateDTOMapper::toDto)
                    .collect(Collectors.toList()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.canAccessSensorState(#id, authentication)")
    public ResponseEntity<SensorStateResponse> getSensorStateById(@PathVariable Integer id) {
        Optional<SensorState> sensorState = sensorStateService.getSensorStateById(id);
        if (sensorState.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(SensorStateDTOMapper.toDto(sensorState.get()));
    }

    /**
     * Retrieves all sensor state entries for a specific sensor within a given date and time range.
     *
     * @param id    the ID of the sensor
     * @param start the start of the date/time range (inclusive), in ISO-8601 format (e.g., 2025-06-25T14:30:00)
     * @param end   the end of the date/time range (inclusive), in ISO-8601 format
     * @return      a {@link ResponseEntity} containing a list of {@link SensorStateResponse} objects if successful,
     *              or an appropriate HTTP status code if an error occurs:
     *              <ul>
     *                  <li>400 BAD_REQUEST if the start time is after the end time or parameters are invalid</li>
     *                  <li>500 INTERNAL_SERVER_ERROR for unexpected errors</li>
     *              </ul>
     *
     * @throws IllegalArgumentException if parameters are invalid
     */
    @GetMapping("/between/{id}")
    @PreAuthorize("@authorizationService.canAccessSensor(#id, authentication)")
    public ResponseEntity<List<SensorStateResponse>> getSensorStatesBetween(Integer id,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            if (start.isAfter(end)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            List<SensorState> states = sensorStateService.getSensorStatesBetween(id, start, end);
            return ResponseEntity.ok(states.stream()
                    .map(SensorStateDTOMapper::toDto)
                    .toList());

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
