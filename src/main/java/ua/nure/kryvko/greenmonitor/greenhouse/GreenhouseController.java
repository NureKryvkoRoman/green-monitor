package ua.nure.kryvko.greenmonitor.greenhouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/greenhouses")
public class GreenhouseController {
    @Autowired
    private GreenhouseService greenhouseService;

    @PostMapping
    public ResponseEntity<GreenhouseResponse> createGreenhouse(@RequestBody Greenhouse greenhouse) {
        try {
            Greenhouse savedGreenhouse = greenhouseService.saveGreenhouse(greenhouse);
            return new ResponseEntity<>(GreenhouseDTOMapper.toDto(savedGreenhouse), HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/usersummary/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<GreenhouseSummary>> getGreenhousesSummaryByUserId(@PathVariable Integer userId) {
        try {
            List<GreenhouseSummary> greenhouses = greenhouseService.getGreenhousesSummaryByUserId(userId);
            return ResponseEntity.ok(greenhouses);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //TODO: add userId deduction via token?
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<GreenhouseResponse>> getGreenhousesByUserId(@PathVariable Integer userId) {
        try {
            List<Greenhouse> greenhouses = greenhouseService.getGreenhousesByUserId(userId);
            return ResponseEntity.ok(greenhouses.stream().map(GreenhouseDTOMapper::toDto).collect(Collectors.toList()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/gdd")
    @PreAuthorize("@authorizationService.canAccessGreenhouse(#id, authentication)")
    public ResponseEntity<Double> getGDD(
            @PathVariable("id") int greenhouseId,
            @RequestParam double baseTemp,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        double gdd = greenhouseService.calculateGDD(greenhouseId, baseTemp, from, to);
        return ResponseEntity.ok(gdd);
    }

    @GetMapping("/{id}/dew-point")
    @PreAuthorize("@authorizationService.canAccessGreenhouse(#id, authentication)")
    public ResponseEntity<Double> getDewPoint(
            @PathVariable("id") int greenhouseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        double dewPoint = greenhouseService.calculateDewPoint(greenhouseId, date);
        return ResponseEntity.ok(dewPoint);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.getUsername()")
    public ResponseEntity<List<GreenhouseResponse>> getGreenhousesByUserEmail(@RequestParam String email) {
        try {
            List<Greenhouse> greenhouses = greenhouseService.getGreenhousesByUserEmail(email);
            return ResponseEntity.ok(greenhouses.stream().map(GreenhouseDTOMapper::toDto).collect(Collectors.toList()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.canAccessGreenhouse(#id, authentication)")
    public ResponseEntity<GreenhouseResponse> getGreenhouseById(@PathVariable Integer id) {
        Optional<Greenhouse> greenhouse = greenhouseService.getGreenhouseById(id);
        if (greenhouse.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(GreenhouseDTOMapper.toDto(greenhouse.get()));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@authorizationService.canAccessGreenhouse(#id, authentication)")
    public ResponseEntity<GreenhouseResponse> updateGreenhouse(@PathVariable Integer id, @RequestBody Greenhouse greenhouse) {
        try {
            Greenhouse updatedGreenhouse = greenhouseService.updateGreenhouse(id, greenhouse);
            return ResponseEntity.ok(GreenhouseDTOMapper.toDto(updatedGreenhouse));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationService.canAccessGreenhouse(#id, authentication)")
    public ResponseEntity<Void> deleteGreenhouse(@PathVariable Integer id) {
        try {
            greenhouseService.deleteGreenhouseById(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
