package edu.kansal_wells_xu_pina.realestate_api.initializers;

import edu.kansal_wells_xu_pina.realestate_api.entities.PropertyImage;
import edu.kansal_wells_xu_pina.realestate_api.entities.Property;
import edu.kansal_wells_xu_pina.realestate_api.entities.User;
import edu.kansal_wells_xu_pina.realestate_api.enums.Role;
import edu.kansal_wells_xu_pina.realestate_api.repositories.PropertyImageRepository;
import edu.kansal_wells_xu_pina.realestate_api.repositories.PropertyRepository;
import edu.kansal_wells_xu_pina.realestate_api.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer {

    private final PropertyRepository propertyRepo;
    private final PropertyImageRepository imageRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public DataInitializer(PropertyRepository propertyRepo,
                           UserRepository userRepo,
                           PropertyImageRepository imageRepo,
                           PasswordEncoder passwordEncoder) {
        this.propertyRepo = propertyRepo;
        this.userRepo = userRepo;
        this.imageRepo = imageRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (userRepo.count() == 0) {


            User u1 = new User(
                    "Default",
                    "Admin",
                    "admin@example.com",
                    passwordEncoder.encode("admin.123"),
                    LocalDateTime.now(),
                    Role.ADMIN
            );

            User u2 = new User("Default",
                    "Agent",
                    "agent@example.com",
                    passwordEncoder.encode("agent.123"),
                    LocalDateTime.now(),
                   Role.AGENT
            );
            User u3 = new User(
                    "Default",
                    "Buyer",
                    "buyer@example.com",
                    passwordEncoder.encode("buy.123"),
                    LocalDateTime.now(),
                    Role.BUYER
            );
            userRepo.saveAll(List.of(u1, u2, u3));

            System.out.println("Initial users and roles inserted.");
        }

        User agent = userRepo.findByEmail("agent@example.com");

        if (propertyRepo.count() > 0) {
            System.out.println("Properties already initialized, skipping.");
            return;
        }

        List<Property> properties = List.of(
                new Property(
                        "3818 N Christiana Ave",
                        1_025_000.0,
                        "Experience luxury living in this beautifully redesigned single-family home, where expert craftsmanship and meticulous attention to detail shine throughout. …",
                        "Chicago, IL 60618",
                        3600,
                        agent
                ),
                new Property(
                        "3423 N Kedzie Ave",
                        899_000.0,
                        "Oversized all-brick single-family home in East Avondale between Logan Square and Irving Park. Approximately 4600 sq. ft. …",
                        "Chicago, IL 60618",
                        4600,
                        agent
                ),
                new Property(
                        "1837 N Fremont St",
                        3_795_000.0,
                        "Welcome to this architectural masterpiece, nestled in the heart of Lincoln Park on the serene, tree-lined Fremont Street. …",
                        "Chicago, IL 60614",
                        4662,
                        agent
                ),
                new Property(
                        "2818 W Wellington Ave",
                        899_000.0,
                        "Experience Unparalleled Luxury! Welcome to this stunning, one-of-a-kind luxury home, where elegance meets modern comfort. …",
                        "Chicago, IL 60618",
                        3000,
                        agent),
                new Property(
                        "3454 W Potomac Ave",
                        959_000.0,
                        "Modern 6 bed, 4.1 bath new construction home on an oversized 30' corner lot! …",
                        "Chicago, IL 60651",
                        4098,
                        agent)
        );

        List<Property> saved = propertyRepo.saveAll(properties);
        saved.forEach(this::attachImages);

        System.out.println("Inserted " + properties.size() + " properties.");
    }

    private void attachImages(Property property) {
        String folderName = property.getTitle();

        String projectRoot = System.getProperty("user.dir");
        Path folder = Paths.get(projectRoot, "src/main/resources/images", folderName);
        if (!Files.exists(folder)) {
            // no images for this property
            return;
        }

        try (DirectoryStream<Path> stream =
                     Files.newDirectoryStream(folder, "*.{jpg,png,webp}")) {
            List<PropertyImage> images = new ArrayList<>();
            for (Path imgPath : stream) {
                PropertyImage pi = new PropertyImage();
                pi.setImageFileName(imgPath.getFileName().toString());
                pi.setProperty(property);
                images.add(pi);
            }
            imageRepo.saveAll(images);
            property.setImages(images);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load images from " + folder, e);
        }

    }
}
