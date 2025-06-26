package ua.nure.kryvko.greenmonitor.greenhouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.nure.kryvko.greenmonitor.auth.CustomUserDetails;

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

    @GetMapping("/summary/my")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<GreenhouseSummary>> getGreenhousesSummaryForUser(Authentication authentication) {
        try {
            Integer userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            List<GreenhouseSummary> greenhouses = greenhouseService.getGreenhousesSummaryByUserId(userId);
            return ResponseEntity.ok(greenhouses);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<GreenhouseResponse>> getGreenhousesByUserId(@PathVariable Integer userId) {
        try {
            List<Greenhouse> greenhouses = greenhouseService.getGreenhousesByUserId(userId);
            return ResponseEntity.ok(greenhouses.stream().map(GreenhouseDTOMapper::toDto).toList());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/my")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<GreenhouseResponse>> getGreenhousesForAuthenticatedUser(Authentication authentication) {
        try {
            Integer userId = ((CustomUserDetails) authentication.getPrincipal()).getId();

            List<Greenhouse> greenhouses = greenhouseService.getGreenhousesByUserId(userId);
            List<GreenhouseResponse> responses = greenhouses.stream()
                    .map(GreenhouseDTOMapper::toDto)
                    .toList();

            return ResponseEntity.ok(responses);
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
