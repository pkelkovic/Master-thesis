package sensor.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import sensor.entity.Location;
import sensor.entity.Reading;
import sensor.entity.SensorInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class SensorService {
    // info about this sensor
    private SensorInfo sensor;
    private String readingUnit;

    // Properties from .yaml files:
    // value from 0 - num_of_sensor_types, determinating what kind of sensor is this
    @Value("${SENSOR_TYPE_ID:0}")
    private Integer sensorTypeID;

    // MQTT service for publishing messages
    @Autowired
    private MQTTService mqttService;

    // MQTT topic where sensors publish their information (once)
    @Value("${MQTT_INFO_TOPIC:sensor/information/}")
    private String infoTopic;
    // MQTT topic where sensors publish their readings
    @Value("${MQTT_READINGS_TOPIC:sensor/data/}")
    private String sensorTopic;

    // Data from readings.csv - first row are sensor types, second one are units, and then data
    ArrayList<String> sensorTypes;
    ArrayList<String> sensorUnits;
    HashMap<Integer, ArrayList<String>> data;

    // variable that tracks if sensor and data have been initialized
    private volatile boolean initialized = false;

    @Autowired
    private ResourceLoader resourceLoader;

    private static final Logger log = LoggerFactory.getLogger(SensorService.class);

    public void initializeSensor() throws IllegalArgumentException {
        // creating new sensor info object
        sensor = new SensorInfo();

        // generate random, unique ID for this sensor
        sensor.setSensorID(UUID.randomUUID().toString());

        int numOfTypes = sensorTypes.size();

        // determinating sensor type
        if (sensorTypeID < 0 || sensorTypeID > (numOfTypes - 1)) throw new IllegalArgumentException("Invalid sensor type.");
        else {
            var type = sensorTypes.get(sensorTypeID);
            sensor.setType(type);
            sensorTopic = sensorTopic + type;
            readingUnit = sensorUnits.get(sensorTypeID);
            sensor.setReadingUnit(readingUnit);
        }

        // setting random model name for sensor
        String model = sensor.getType().substring(0, 2).toUpperCase() + "-" + String.format("%02d",
        ThreadLocalRandom.current().nextInt(100));
        sensor.setModel(model);

        // setting random location in the warehouse for sensor
        Location sensorLocation = new Location();
        sensorLocation.setZone("Z" + String.format("%1d", ThreadLocalRandom.current().nextInt(10)));
        sensorLocation.setAisle("A" + String.format("%02d", ThreadLocalRandom.current().nextInt(21)));
        sensorLocation.setShelf("S" + String.format("%02d", ThreadLocalRandom.current().nextInt(100)));
        sensor.setLocation(sensorLocation);

        return;
    }

    //initialize data from readings.csv
    public void initializeData() {
        data = new HashMap<Integer, ArrayList<String>>();
        try {
            Resource resource = resourceLoader.getResource("classpath:readings.csv");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            int i = 0;
            while (line != null) {
                var list = line.split(",");
                var help = new ArrayList<String>();
                for(var s: list ) {
                    help.add(s);
                }
                // first row in readings.csv represents sensor types (temp, humidity, ...)
                if (i == 0) {
                    sensorTypes = new ArrayList<>();
                    sensorTypes.addAll(help);
                }
                // second row in readings.csv represents sensor units (C, %, kg,...) 
                else if (i == 1) {
                    sensorUnits = new ArrayList<>();
                    sensorUnits.addAll(help);
                }
                // all of the other rows are containing data for reading simulations
                else {
                    data.put(i-2, help);
                }
                i ++;
                line = reader.readLine();
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // function that simulates reading temperature and puting data into TemperatureReading object
    public Reading getReading() {
        // simulation of sensor's work --> getting random row no. and reading data from that row
        int rowNum = ThreadLocalRandom.current().nextInt(data.size() - 1);
        var row = data.get(rowNum);
        // default value (-1) --> a sign that something went wrong with the reading
        Double value = Double.parseDouble(row.get(sensorTypeID));

        //creating new instance of Reading
        Reading reading = new Reading();
        reading.setSensorID(sensor.getSensorID());
        reading.setReadingUnit(readingUnit);
        reading.setReadingID(UUID.randomUUID().toString());
        reading.setReadingValue(value);
        reading.setTimestamp(Instant.now());

        return reading;
    }

    //function that is executed every PERIOD miliseconds, generates reading and sends it to MQTT topic
    @Scheduled(fixedRateString = "${PERIOD:30000}")
    public void readData() {
        //if csv and sensor data isn't already initialized, initialize it
        if (!initialized) {
            initializeData();
            initializeSensor();
            initialized = true;
            // send sensor's general info
            // information about each sensor is being send only once to aggregator, at the very beginning
            log.info("[INFO] Sensor's information: \nID: " + sensor.getSensorID() + "\nType: "+sensor.getType() + "\nReading Unit: "+sensor.getReadingUnit()+"\nModel: "+sensor.getModel()+ "\nLocation: "+sensor.getLocation() +"\n");
            mqttService.publishMessage(infoTopic + sensor.getSensorID(), sensor);
        }
        // "read" value from the outside
        var reading = getReading();

        log.info("[READING] Sensor's measurements: " + reading.getReadingValue() + " "
                    + reading.getReadingUnit());

        // publish reading to topic of this sensor
        mqttService.publishMessage(sensorTopic, reading);
    
        return;
    }

}
