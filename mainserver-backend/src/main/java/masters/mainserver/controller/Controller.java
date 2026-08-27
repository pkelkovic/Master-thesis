package masters.mainserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import masters.mainserver.entity.SensorInfo;
import masters.mainserver.entity.SensorStats;
import masters.mainserver.entity.Status;
import masters.mainserver.service.DataService;

@CrossOrigin(origins = "*")
@RestController
public class Controller {

    @Autowired
    private DataService dataService;

    @GetMapping("/sensors/info")
    public List<SensorInfo> getSensorsInfo() {
        return dataService.getSensorInfo();
    }

    @GetMapping("/sensors/{id}/stats")
    public List<SensorStats> getSensorsStats(@PathVariable String id) {
        return dataService.getSensorStats(id);
    }

    @GetMapping("/sensors/errors")
    public List<String> getSensorErrors() {
        return dataService.getErrorMessages();
    }

    @GetMapping("/sensors/{id}/status")
    public Status getSensorsStatus(@PathVariable String id) {
        return dataService.getSensorStatus(id);
    }
    
}
