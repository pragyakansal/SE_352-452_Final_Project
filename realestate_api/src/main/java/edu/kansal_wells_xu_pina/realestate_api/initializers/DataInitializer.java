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
        System.out.println("Running Data Initializer...");

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
                        agent),
                new Property(
                        "461 W Melrose St",
                        3300000.0,
                        "East Lakeview is the setting for this gorgeous contemporary masterpiece. The home is comprised of four levels of living space. …",
                        "Chicago, IL 60657",
                        5400,
                        agent),
                new Property(
                        "1741 N Mozart St", 849000.0,
                        "Reimagined Logan Square single-family home that's been carefully curated by the owner/designer. …",
                        "Chicago, IL 60647",
                        2631,
                        agent),
                new Property(
                        "2317 W Ohio St",
                        949000.0,
                        "Fully gut-rehabbed in 2022, this 4 bedroom, 3.1 bath, 3000 SQFT single family home ideally located in Chicago's Ukrainian Village neighborhood. …",
                        "Chicago, IL 60612",
                        3000,
                        agent
                ),
                new Property(
                        "1701 N Dayton St",
                        4750000.0, "Stunning LG custom-built single-family home on an extra wide 36.5' lot on a quiet stretch of Dayton in the heart of Lincoln Park. …",
                        "Chicago, IL 60614",
                        8000,
                        agent
                ),
                new Property(
                        "334 N Jefferson St UNIT D",
                        925000.0,
                        "This rare corner townhome in Kinzie Station offers 3 bedrooms and 3 baths, showcasing a renovated kitchen and an open-concept design.  …",
                        "Chicago, IL 60661",
                        2600,
                        agent
                ),
                new Property(
                        "1249 S Plymouth Ct",
                        1200000.0,
                        "Welcome home to your urban oasis in this rarely available single family home in Chicago's South Loop! …",
                        "Chicago, IL 60605",
                        3000,
                        agent
                ),
                new Property(
                        "2779 N Kenmore Ave",
                        1300000.0,
                        "Unicorn alert! This rare, beautifully renovated Victorian in the heart of Lincoln Park seamlessly blends historic craftsmanship with contemporary elegance, offering a truly one-of-a-kind living experience. …",
                        "Chicago, IL 60614",
                        2532,
                        agent
                ),
                new Property(
                        "4425 N Winchester Ave",
                        1125000.0,
                        "Lovely Victorian home on large lot in Ravenswood. This very wide home offers formal foyer, living and dining rooms. …",
                        "Chicago, IL 60640",
                        3000,
                        agent
                ),
                new Property(
                        "4511 N Saint Louis Ave",
                        889000.0,
                        "A perfect blend of traditional charm and modern elegance, this fully rebuilt contemporary home sits on a generous 38' wide lot, featuring a wrap-around porch and rear deck. …",
                        "Chicago, IL 60625",
                        3213,
                        agent
                ),
                new Property(
                        "401 W Dickens Ave",
                        5995000.0,
                        "This ultra notable single-family home sits on an extra-wide lot at the corner of Sedgwick and Dickens! The setting of the home allows for open views outside. …",
                        "Chicago, IL 60614",
                        7252,
                        agent
                ),
                new Property (
                        "339 W Webster Ave UNIT 2B",
                        1225000.0,
                        "Enjoy the perfect, prime East Lincoln Park location in this wonderfully intimate gated townhome community. …",
                        "Chicago, IL 60614",
                        2400,
                        agent
                ),
                new Property (
                        "1541 W Addison St", 1200000.0,
                        "Don't miss this pristine, beautifully rehabbed solid brick home just steps to the highly desirable Southport Corridor. …",
                        "Chicago, IL 60613",
                        2869,
                        agent
                )
        );

        List<Property> saved = propertyRepo.saveAll(properties);
        saved.forEach(this::attachImages);

        System.out.println("Inserted " + properties.size() + " properties.");
    }

    private void attachImages(Property property) {
        String folderName = property.getTitle();

        String projectRoot = System.getProperty("user.dir");
        Path folder = Paths.get(projectRoot, "src/main/resources/static/images/property_images", folderName);
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
