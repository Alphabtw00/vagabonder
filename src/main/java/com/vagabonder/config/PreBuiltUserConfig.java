package com.vagabonder.config;

import com.vagabonder.entity.Itinerary;
import com.vagabonder.entity.User;
import com.vagabonder.enums.Gender;
import com.vagabonder.enums.ItineraryStatus;
import com.vagabonder.repository.ItineraryRepository;
import com.vagabonder.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class PreBuiltUserConfig implements CommandLineRunner{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ItineraryRepository itineraryRepository;

    public PreBuiltUserConfig(UserRepository userRepository, PasswordEncoder passwordEncoder, ItineraryRepository itineraryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.itineraryRepository = itineraryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<User> users = List.of(
                User.builder()
                        .email("ansh@gmail.com")
                        .password(passwordEncoder.encode("12345"))
                        .fullName("Ansh Anand")
                        .profilePhotoPath("/images/ansh-profile.gif")
                        .coverPhotoPath("/images/ansh-cover.gif")
                        .dateOfBirth(LocalDate.of(2003, 11, 10))
                        .gender(Gender.MALE)
                        .residence("Delhi")
                        .languages("English, Hindi")
                        .ethnicity("Indian")
                        .religion("Hindu")
                        .occupation("Student")
                        .placesTravelled("Kerala, Goa")
                        .bio("Loves adventure!")
                        .bestTravelStory("Hiking the Alps solo.")
                        .build(),

                User.builder()
                        .email("aditya@gmail.com")
                        .password(passwordEncoder.encode("12345"))
                        .fullName("Aditya Bhardwaj")
                        .profilePhotoPath("/images/aditya-profile.jpeg")
                        .coverPhotoPath("/images/aditya-cover.jpg")
                        .travelMemoryPaths(List.of("/images/travelmemory1.jpeg","/images/travelmemory2.jpeg","/images/travelmemory3.jpeg","/images/travelmemory4.jpeg","/images/travelmemory5jpeg","/images/travelmemory6.jpeg"))
                        .dateOfBirth(LocalDate.of(2003, 4, 6))
                        .gender(Gender.MALE)
                        .residence("Gurugram")
                        .languages("English, Hindi")
                        .ethnicity("Indian")
                        .religion("Hindu")
                        .occupation("Student")
                        .placesTravelled("Manali, Jaipur")
                        .bio("Exploring life, one road trip at a time.")
                        .bestTravelStory("Getting lost in the vibrant streets of Jaipur, only to stumble upon the best local food!")
                        .build(),

                User.builder()
                        .email("ayushi@gmail.com")
                        .password(passwordEncoder.encode("12345"))
                        .fullName("Ayushi Singh")
                        .profilePhotoPath("/images/ayushi-profile.jpeg")
                        .coverPhotoPath("/images/ayushi-cover.jpg")
                        .dateOfBirth(LocalDate.of(2003, 5, 29))
                        .gender(Gender.FEMALE)
                        .residence("Delhi")
                        .languages("Hindi")
                        .ethnicity("Indian")
                        .religion("Hindu")
                        .occupation("Student")
                        .placesTravelled("India Only")
                        .bio("Enjoying Life")
                        .bestTravelStory("Getting lost in the vibrant streets of Jaipur, only to stumble upon the best local food!")
                        .build(),

                User.builder()
                        .email("sezal@gmail.com")
                        .password(passwordEncoder.encode("12345"))
                        .fullName("Sezal Sharma")
                        .profilePhotoPath("/images/sezal-profile.jpg")
                        .coverPhotoPath("/images/sezal-cover.jpg")
                        .dateOfBirth(LocalDate.of(2003, 7, 22))
                        .gender(Gender.FEMALE)
                        .residence("Jaipur")
                        .languages("English, Hindi")
                        .ethnicity("Indian")
                        .religion("Hindu")
                        .occupation("Student")
                        .placesTravelled("Udaipur, Agra")
                        .bio("Passionate about capturing moments through my lens.")
                        .bestTravelStory("Exploring the serene lakes of Udaipur during a solo trip.")
                        .build(),
//                User.builder()
//                        .email("shreshtha@gmail.com")
//                        .password(passwordEncoder.encode("12345"))
//                        .fullName("Shreshtha Sharma")
//                        .profilePhotoPath("/images/shreshtha-profile.jpg")
//                        .coverPhotoPath("/images/shreshtha-cover.jpg")
//                        .dateOfBirth(LocalDate.of(2003, 10, 8))
//                        .gender(Gender.FEMALE)
//                        .residence("Chandigarh")
//                        .languages("English, Hindi, Punjabi")
//                        .ethnicity("Indian")
//                        .religion("Hindu")
//                        .occupation("Student")
//                        .placesTravelled("Amritsar, Dalhousie")
//                        .bio("Lover of books, chai, and mountains.")
//                        .bestTravelStory("Witnessing the Golden Temple at sunrise—it was a spiritual awakening.")
//                        .build(),
                User.builder()
                        .email("kartikeya@gmail.com")
                        .password(passwordEncoder.encode("12345"))
                        .fullName("Kartikeya Rajput")
                        .profilePhotoPath("/images/kartikeya-profile.jpg")
                        .coverPhotoPath("/images/kartikeya-cover.jpg")
                        .dateOfBirth(LocalDate.of(2002, 12, 15))
                        .gender(Gender.MALE)
                        .residence("Delhi")
                        .languages("English, Hindi")
                        .ethnicity("Indian")
                        .religion("Hindu")
                        .occupation("Student")
                        .placesTravelled("Leh, Ladakh")
                        .bio("Adventure enthusiast with a love for the unknown.")
                        .bestTravelStory("Biking through the rugged terrains of Ladakh was a lifetime experience.")
                        .build(),
                User.builder()
                        .email("kushagra@gmail.com")
                        .password(passwordEncoder.encode("12345"))
                        .fullName("Kushagra Goyal")
                        .profilePhotoPath("/images/kushagra-profile.jpg")
                        .coverPhotoPath("/images/kushagra-cover.jpg")
                        .dateOfBirth(LocalDate.of(2003, 1, 20))
                        .gender(Gender.MALE)
                        .residence("Noida")
                        .languages("English, Hindi")
                        .ethnicity("Indian")
                        .religion("Hindu")
                        .occupation("Student")
                        .placesTravelled("Mumbai, Goa")
                        .bio("Foodie, movie buff, and a beach lover.")
                        .bestTravelStory("Experiencing the vibrant nightlife of Goa was unforgettable.")
                        .build(),
//                User.builder()
//                        .email("diya@gmail.com")
//                        .password(passwordEncoder.encode("12345"))
//                        .fullName("Diya Arora")
//                        .profilePhotoPath("/images/diya-profile.jpg")
//                        .coverPhotoPath("/images/diya-cover.jpg")
//                        .dateOfBirth(LocalDate.of(2003, 9, 30))
//                        .gender(Gender.FEMALE)
//                        .residence("Pune")
//                        .languages("English, Hindi")
//                        .ethnicity("Indian")
//                        .religion("Hindu")
//                        .occupation("Student")
//                        .placesTravelled("Lonavala, Mahabaleshwar")
//                        .bio("Nature lover and amateur artist.")
//                        .bestTravelStory("Sketching the sunset view at Mahabaleshwar was pure bliss.")
//                        .build(),
//                User.builder()
//                        .email("amartya@gmail.com")
//                        .password(passwordEncoder.encode("12345"))
//                        .fullName("Amartya Pratap Singh")
//                        .profilePhotoPath("/images/amartya-profile.jpg")
//                        .coverPhotoPath("/images/amartya-cover.jpg")
//                        .dateOfBirth(LocalDate.of(2003, 3, 18))
//                        .gender(Gender.MALE)
//                        .residence("Lucknow")
//                        .languages("English, Hindi")
//                        .ethnicity("Indian")
//                        .religion("Hindu")
//                        .occupation("Student")
//                        .placesTravelled("Varanasi, Allahabad")
//                        .bio("Philosopher at heart, traveler by passion.")
//                        .bestTravelStory("Boat rides at dawn in Varanasi gave me peace like never before.")
//                        .build(),
                User.builder()
                        .email("prashasti@gmail.com")
                        .password(passwordEncoder.encode("12345"))
                        .fullName("Prashasti Bhardwaj")
                        .profilePhotoPath("/images/prashasti-profile.jpg")
                        .coverPhotoPath("/images/prashasti-cover.jpg")
                        .dateOfBirth(LocalDate.of(2003, 8, 24))
                        .gender(Gender.FEMALE)
                        .residence("Bhopal")
                        .languages("English, Hindi")
                        .ethnicity("Indian")
                        .religion("Hindu")
                        .occupation("Student")
                        .placesTravelled("Khajuraho, Pachmarhi")
                        .bio("Believer in wanderlust and spontaneity.")
                        .bestTravelStory("Exploring the historic temples of Khajuraho was fascinating.")
                        .build()
//                User.builder()
//                        .email("divit@gmail.com")
//                        .password(passwordEncoder.encode("12345"))
//                        .fullName("Divit Gaur")
//                        .profilePhotoPath("/images/divit-profile.jpg")
//                        .coverPhotoPath("/images/divit-cover.jpg")
//                        .dateOfBirth(LocalDate.of(2003, 2, 14))
//                        .gender(Gender.MALE)
//                        .residence("Ghaziabad")
//                        .languages("English, Hindi")
//                        .ethnicity("Indian")
//                        .religion("Hindu")
//                        .occupation("Student")
//                        .placesTravelled("Dehradun, Nainital")
//                        .bio("Dream big, travel bigger.")
//                        .bestTravelStory("Kayaking in Nainital was thrilling!")
//                        .build(),
//                User.builder()
//                        .email("aryan@gmail.com")
//                        .password(passwordEncoder.encode("12345"))
//                        .fullName("Aryan Gupta")
//                        .profilePhotoPath("/images/aryan-profile.jpg")
//                        .coverPhotoPath("/images/aryan-cover.jpg")
//                        .dateOfBirth(LocalDate.of(2003, 6, 25))
//                        .gender(Gender.MALE)
//                        .residence("Kanpur")
//                        .languages("English, Hindi")
//                        .ethnicity("Indian")
//                        .religion("Hindu")
//                        .occupation("Student")
//                        .placesTravelled("Varanasi, Rishikesh")
//                        .bio("Fitness enthusiast and a sucker for scenic views.")
//                        .bestTravelStory("River rafting in Rishikesh was pure adrenaline!")
//                        .build(),
//                User.builder()
//                        .email("ayush@gmail.com")
//                        .password(passwordEncoder.encode("12345"))
//                        .fullName("Ayush Mehan")
//                        .profilePhotoPath("/images/ayush-profile.jpg")
//                        .coverPhotoPath("/images/ayush-cover.jpg")
//                        .dateOfBirth(LocalDate.of(2003, 7, 5))
//                        .gender(Gender.MALE)
//                        .residence("Jalandhar")
//                        .languages("English, Hindi, Punjabi")
//                        .ethnicity("Indian")
//                        .religion("Hindu")
//                        .occupation("Student")
//                        .placesTravelled("Chandigarh, Amritsar")
//                        .bio("Loves food, festivals, and fun travels.")
//                        .bestTravelStory("Attending the Wagah Border ceremony was electrifying!")
//                        .build(),
//                User.builder()
//                        .email("utkarsh@gmail.com")
//                        .password(passwordEncoder.encode("12345"))
//                        .fullName("Utkarsh Arora")
//                        .profilePhotoPath("/images/utkarsh-profile.jpg")
//                        .coverPhotoPath("/images/utkarsh-cover.jpg")
//                        .dateOfBirth(LocalDate.of(2003, 10, 12))
//                        .gender(Gender.MALE)
//                        .residence("Faridabad")
//                        .languages("English, Hindi")
//                        .ethnicity("Indian")
//                        .religion("Hindu")
//                        .occupation("Student")
//                        .placesTravelled("Rishikesh, Shimla")
//                        .bio("A budding writer inspired by the beauty of the Himalayas.")
//                        .bestTravelStory("Writing poetry on the banks of the Ganga was surreal.")
//                        .build(),
//                User.builder()
//                        .email("daksh@gmail.com")
//                        .password(passwordEncoder.encode("12345"))
//                        .fullName("Daksh Lohia")
//                        .profilePhotoPath("/images/daksh-profile.jpg")
//                        .coverPhotoPath("/images/daksh-cover.jpg")
//                        .dateOfBirth(LocalDate.of(2003, 11, 3))
//                        .gender(Gender.MALE)
//                        .residence("Indore")
//                        .languages("English, Hindi")
//                        .ethnicity("Indian")
//                        .religion("Hindu")
//                        .occupation("Student")
//                        .placesTravelled("Mysore, Coorg")
//                        .bio("Tea lover and mountain chaser.")
//                        .bestTravelStory("Trekking through the lush greenery of Coorg was refreshing.")
//                        .build()
        );

        userRepository.saveAll(users);


        User ansh = userRepository.findByEmailEquals("ansh@gmail.com").orElseThrow(() -> new RuntimeException("User not found"));

        // Create itineraries for Ansh Anand
        List<Itinerary> itineraries = List.of(
                Itinerary.builder()
                        .name("Exploring Kerala")
                        .activities("Day 1: Houseboat stay in Alleppey, Day 2: Visit Munnar tea plantations, Day 3: Explore Fort Kochi.")
                        .user(ansh)
                        .fromDate(LocalDate.of(2024, 1, 15))
                        .toDate(LocalDate.of(2024, 1, 18))
                        .status(ItineraryStatus.COMPLETED)
                        .build(),

                Itinerary.builder()
                        .name("Beach Hopping in Goa")
                        .activities("Day 1: Relax at Baga Beach, Day 2: Visit Old Goa churches, Day 3: Sunset cruise on Mandovi River.")
                        .user(ansh)
                        .fromDate(LocalDate.of(2024, 2, 10))
                        .toDate(LocalDate.of(2024, 2, 12))
                        .status(ItineraryStatus.PLANNED)
                        .build()
        );

        itineraryRepository.saveAll(itineraries);
    }
}

