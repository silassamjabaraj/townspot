package com.example.data.seed

import com.example.data.dao.BusinessDao
import com.example.data.dao.CategoryDao
import com.example.data.dao.NeighborhoodDao
import com.example.data.dao.PromotionDao
import com.example.data.dao.ReviewDao
import com.example.data.dao.UserDao
import com.example.data.model.BusinessEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.NeighborhoodEntity
import com.example.data.model.PromotionEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseInitializer {

    val DEFAULT_CATEGORIES = listOf(
        CategoryEntity(name = "Cafes & Bakeries", iconName = "local_cafe", description = "Coffee houses, artisan roasters, and fresh pastry bakehouses"),
        CategoryEntity(name = "Restaurants", iconName = "restaurant", description = "Fine dining, bistros, casual eateries, and local flavors"),
        CategoryEntity(name = "Gymnasium", iconName = "fitness_center", description = "Fitness clubs, personal training, and workout facilities"),
        CategoryEntity(name = "Salon & Spa", iconName = "content_cut", description = "Hair stylists, barber shops, nails, and grooming lounges"),
        CategoryEntity(name = "Hospital & Clinics", iconName = "local_hospital", description = "Medical centers, dental practices, and community healthcare"),
        CategoryEntity(name = "Retail & Shopping", iconName = "shopping_bag", description = "Boutiques, independent bookshops, and neighborhood markets"),
        CategoryEntity(name = "Health & Wellness", iconName = "spa", description = "Yoga studios, meditation centers, and holistic therapy"),
        CategoryEntity(name = "Home Services", iconName = "home_repair_service", description = "Plumbing, electrical, woodworking, and interior craft"),
        CategoryEntity(name = "Automotive", iconName = "directions_car", description = "Auto repair, diagnostics, detailing, and tire services"),
        CategoryEntity(name = "Arts & Culture", iconName = "palette", description = "Ceramic ateliers, art galleries, and craft workshops")
    )

    val DEFAULT_NEIGHBORHOODS = listOf(
        NeighborhoodEntity(name = "Downtown", description = "The bustling commercial core, historic brick facades, and vibrant foot-traffic avenues"),
        NeighborhoodEntity(name = "Arts District", description = "Creative hub packed with artist studios, indie galleries, and roaster cafes"),
        NeighborhoodEntity(name = "Riverside", description = "Scenic waterfront district with open-air dining and outdoor pathways"),
        NeighborhoodEntity(name = "Northside", description = "Quiet leafy residential enclave with beloved neighborhood markets and bakeries"),
        NeighborhoodEntity(name = "West End", description = "Upbeat trendy area featuring modern boutiques, specialty shops, and fitness hubs"),
        NeighborhoodEntity(name = "Old Town", description = "Historic preservation quarter with heritage craftsmanship and authentic diners"),
        NeighborhoodEntity(name = "Port Blair Central", description = "Island capital hub connecting travelers, port markets, and island commerce"),
        NeighborhoodEntity(name = "Havelock Island", description = "World-famous turquoise beaches, diving centers, and beachfront resorts")
    )

    suspend fun seedCategoriesAndNeighborhoods(
        categoryDao: CategoryDao,
        neighborhoodDao: NeighborhoodDao,
        force: Boolean = false
    ) {
        if (force || categoryDao.getCategoryCount() == 0) {
            if (force) categoryDao.deleteAllCategories()
            categoryDao.insertAll(DEFAULT_CATEGORIES)
        }
        if (force || neighborhoodDao.getNeighborhoodCount() == 0) {
            if (force) neighborhoodDao.deleteAllNeighborhoods()
            neighborhoodDao.insertAll(DEFAULT_NEIGHBORHOODS)
        }
    }

    suspend fun seedIfEmpty(
        businessDao: BusinessDao,
        reviewDao: ReviewDao,
        userDao: UserDao? = null,
        promotionDao: PromotionDao? = null,
        categoryDao: CategoryDao? = null,
        neighborhoodDao: NeighborhoodDao? = null,
        force: Boolean = false
    ) {
        withContext(Dispatchers.IO) {
            if (categoryDao != null && neighborhoodDao != null) {
                seedCategoriesAndNeighborhoods(categoryDao, neighborhoodDao, force)
            }
            userDao?.let { uDao ->
                if (uDao.getUserCount() == 0) {
                    val defaultUsers = listOf(
                        UserEntity(
                            email = "resident@townspot.com",
                            fullName = "Alex Morgan",
                            passwordHash = "password123",
                            role = "Resident"
                        ),
                        UserEntity(
                            email = "owner@townspot.com",
                            fullName = "Elena Rostova",
                            passwordHash = "password123",
                            role = "Business Owner",
                            businessName = "Artisan Roasters & Brews"
                        ),
                        UserEntity(
                            email = "admin@townspot.com",
                            fullName = "Sarah Chen",
                            passwordHash = "admin",
                            role = "Admin"
                        )
                    )
                    defaultUsers.forEach { u ->
                        try {
                            uDao.insertUser(u)
                        } catch (_: Exception) {}
                    }
                }
            }

            val count = businessDao.getBusinessCount()
            if (!force && count > 0) {
                val sample = businessDao.getBusinessByIdDirect(1)
                if (sample != null && sample.name == "Elite Edge Gymnasium") {
                    return@withContext
                }
            }
            try {
                businessDao.deleteAllBusinesses()
            } catch (_: Exception) {}

            val initialBusinesses = listOf(
                BusinessEntity(
                    id = 1,
                    name = "Elite Edge Gymnasium",
                    category = "Gymnasium",
                    tagline = "High performance fitness center, Olympic lifting & holistic wellness",
                    description = "Elite Edge Gymnasium in Uttara is not just a fitness center; it's a haven for those seeking to transform their lives through physical fitness and holistic wellness. Advanced diagnostic screening is a comprehensive service designed to provide a detailed analysis of your health.",
                    address = "456 Wellness Blvd, Monroe, NC, USA",
                    neighborhood = "Downtown",
                    phone = "(555) 789-0123",
                    website = "https://eliteedgegym.com",
                    email = "james@eliteedgegym.com",
                    hours = "Mon-Sun: 5:00 AM - 11:00 PM",
                    isOpenNow = true,
                    priceLevel = "$30–$250.00",
                    rating = 4.8f,
                    reviewCount = 100,
                    isVerified = true,
                    isFeatured = true,
                    isSaved = false,
                    latitude = 37.7749,
                    longitude = -122.4194,
                    specialOffer = "First month all-access membership 50% off with personal trainer assessment.",
                    tags = "Olympic Barbells, CrossFit, Sauna, Locker Rooms, Cardio Deck, Certified Coaches",
                    categoryIconName = "gym",
                    accentColorHex = "#FF5733",
                    imageResName = "biz_gymnasium"
                ),
                BusinessEntity(
                    id = 2,
                    name = "Modern Barber Salon",
                    category = "Salon",
                    tagline = "Precision grooming, contemporary styling & hot lather shaves",
                    description = "Premier modern barbershop offering precision fades, signature hot towel straight-razor shaves, beard detailing, and luxury hair care treatments in a relaxed lounge atmosphere.",
                    address = "128 Grand Avenue, Jakarta Internation..",
                    neighborhood = "Riverside",
                    phone = "(555) 654-3210",
                    website = "https://modernbarbersalon.com",
                    email = "appointments@modernbarber.com",
                    hours = "Tue-Sat: 9:00 AM - 8:00 PM | Sun: 10:00 AM - 5:00 PM",
                    isOpenNow = true,
                    priceLevel = "$30–$250.00",
                    rating = 4.9f,
                    reviewCount = 85,
                    isVerified = true,
                    isFeatured = true,
                    isSaved = true,
                    latitude = 37.7812,
                    longitude = -122.4089,
                    specialOffer = "Signature haircut + beard sculpt combo with complimentary craft espresso.",
                    tags = "Hot Lather Shaves, Beard Trim, Walk-ins Welcome, Espresso Bar, Premium Pomades",
                    categoryIconName = "salon",
                    accentColorHex = "#8B5CF6",
                    imageResName = "biz_barber"
                ),
                BusinessEntity(
                    id = 3,
                    name = "Life Care Hospital",
                    category = "Hospital",
                    tagline = "Advanced diagnostic screening, multi-specialty outpatient & emergency",
                    description = "Comprehensive regional healthcare campus featuring advanced diagnostic medical screening, multi-specialty care clinics, cardiology, orthopedics, physical therapy, and 24/7 acute emergency care.",
                    address = "800 Health Parkway, Jakarta Internation..",
                    neighborhood = "Northside",
                    phone = "(555) 321-4567",
                    website = "https://lifecarehospital.org",
                    email = "info@lifecarehealth.org",
                    hours = "Open 24/7 (Emergency & Clinics)",
                    isOpenNow = true,
                    priceLevel = "$30–$250.00",
                    rating = 4.8f,
                    reviewCount = 140,
                    isVerified = true,
                    isFeatured = true,
                    isSaved = false,
                    latitude = 37.7901,
                    longitude = -122.4150,
                    specialOffer = "Walk-in preventive wellness checkup package with rapid laboratory report.",
                    tags = "24/7 Emergency, Diagnostic Imaging, Outpatient Pharmacy, Specialist Clinics, Valet Parking",
                    categoryIconName = "hospital",
                    accentColorHex = "#0284C7",
                    imageResName = "biz_wellness"
                ),
                BusinessEntity(
                    id = 4,
                    name = "Fresh Food Restaurant",
                    category = "Restaurant",
                    tagline = "Farm-to-table organic kitchen, craft cocktails & open terrace",
                    description = "Vibrant culinary destination celebrating fresh seasonal harvests, house-made pastas, wood-fired flatbreads, and handcrafted botanical cocktails served in a bright, modern indoor-outdoor dining room.",
                    address = "350 Culinary Promenade, Jakarta Internation..",
                    neighborhood = "Downtown",
                    phone = "(555) 891-2345",
                    website = "https://freshfoodrestaurant.com",
                    email = "reservations@freshfoodrestaurant.com",
                    hours = "Tue-Sun: 11:30 AM - 10:00 PM | Mon: Closed",
                    isOpenNow = true,
                    priceLevel = "$30–$250.00",
                    rating = 4.9f,
                    reviewCount = 120,
                    isVerified = true,
                    isFeatured = true,
                    isSaved = false,
                    latitude = 37.7749,
                    longitude = -122.4194,
                    specialOffer = "Weekday lunch special: 20% off all artisan bowls and fresh catch entrees.",
                    tags = "Outdoor Patio, Vegan & Gluten-Free Options, Organic Wine, Live Music, Craft Cocktails",
                    categoryIconName = "restaurant",
                    accentColorHex = "#10B981",
                    imageResName = "biz_bistro"
                ),
                BusinessEntity(
                    id = 5,
                    name = "Artisan Roasters & Brews",
                    category = "Cafes & Bakeries",
                    tagline = "Single-origin pour-overs, artisanal pastries & cozy loft seating",
                    description = "A beloved neighborhood sanctuary for coffee purists and creative locals. We roast direct-trade beans in small batches twice a week and serve house-baked almond croissants, matcha lattes, and nitro cold brew in our light-filled heritage brick space.",
                    address = "142 Maplewood Avenue, Suite 101",
                    neighborhood = "Downtown",
                    phone = "(555) 234-5678",
                    website = "https://artisanroastersbrew.com",
                    email = "hello@artisanroasters.com",
                    hours = "Mon-Fri: 6:30 AM - 6:00 PM | Sat-Sun: 7:30 AM - 5:00 PM",
                    isOpenNow = true,
                    priceLevel = "$15–$45.00",
                    rating = 4.9f,
                    reviewCount = 84,
                    isVerified = true,
                    isFeatured = false,
                    isSaved = true,
                    latitude = 37.7749,
                    longitude = -122.4194,
                    specialOffer = "Free cold brew upgrade or drip coffee with any warm pastry purchase before 10 AM.",
                    tags = "Free Fast WiFi, Outdoor Patio, Dog Friendly, Vegan Options, Power Outlets",
                    categoryIconName = "cafe",
                    accentColorHex = "#92400E",
                    imageResName = "biz_cafe",
                    claimedByOwnerEmail = "owner@townspot.com",
                    claimedByOwnerName = "Elena Rostova",
                    isClaimed = true
                ),
                BusinessEntity(
                    id = 3,
                    name = "Timber & Forge Woodcraft",
                    category = "Home Services",
                    tagline = "Custom solid hardwood furniture, architectural cabinetry & repairs",
                    description = "Family-owned wood workshop hand-crafting dining tables, walnut credenzas, floating shelves, and custom kitchen islands using locally sourced reclaimed and sustainably harvested timber.",
                    address = "88 Foundry Row, Studio 4",
                    neighborhood = "Arts District",
                    phone = "(555) 745-3210",
                    website = "https://timberandforgedesign.com",
                    email = "workshop@timberandforge.com",
                    hours = "Mon-Fri: 8:00 AM - 5:00 PM | Sat: By Appointment",
                    isOpenNow = true,
                    priceLevel = "$$$$",
                    rating = 5.0f,
                    reviewCount = 38,
                    isVerified = true,
                    isFeatured = false,
                    isSaved = false,
                    latitude = 37.7650,
                    longitude = -122.4200,
                    specialOffer = "Free in-home design consultation & 10% off your first custom commission.",
                    tags = "Custom Made, Eco-Friendly, Free Estimates, Licensed & Insured, Local Craftsmen",
                    categoryIconName = "carpenter",
                    accentColorHex = "#78350F",
                    imageResName = "biz_woodcraft"
                ),
                BusinessEntity(
                    id = 4,
                    name = "Aura Wellness & Yoga Sanctuary",
                    category = "Health & Wellness",
                    tagline = "Mindful yoga, infrared sound baths & holistic acupuncture",
                    description = "A tranquil wellness center dedicated to physical renewal and mental peace. Offering daily heated vinyasa flows, yin yoga, infrared sauna suites, crystal sound baths, and an organic herbal tea dispensary.",
                    address = "220 Cedar Terrace, 2nd Floor",
                    neighborhood = "Northside",
                    phone = "(555) 432-8765",
                    website = "https://aurawellnesscenter.com",
                    email = "namaste@aurawellness.com",
                    hours = "Mon-Sun: 6:00 AM - 8:30 PM",
                    isOpenNow = true,
                    priceLevel = "$$",
                    rating = 4.9f,
                    reviewCount = 53,
                    isVerified = true,
                    isFeatured = true,
                    isSaved = true,
                    latitude = 37.7901,
                    longitude = -122.4150,
                    specialOffer = "New Student Discovery Pass: 7 days of unlimited community classes for only $29.",
                    tags = "Shower Facilities, Mat Rental, Tea Lounge, Beginner Friendly, Meditation",
                    categoryIconName = "spa",
                    accentColorHex = "#0D9488",
                    imageResName = "biz_wellness"
                ),
                BusinessEntity(
                    id = 5,
                    name = "Apex Auto & Hybrid Tuning",
                    category = "Automotive",
                    tagline = "Honest, ASE-certified mechanical repairs, EV diagnostics & brakes",
                    description = "Serving local drivers for over 22 years. Transparent pricing, digital inspection reports with photos sent directly to your phone, free loaner bicycles, and warranty-backed service on all makes and models.",
                    address = "730 Industrial Parkway",
                    neighborhood = "West End",
                    phone = "(555) 678-9012",
                    website = "https://apexautoworks.com",
                    email = "service@apexautoworks.com",
                    hours = "Mon-Fri: 7:30 AM - 6:00 PM | Sat: 8:00 AM - 2:00 PM",
                    isOpenNow = true,
                    priceLevel = "$$",
                    rating = 4.7f,
                    reviewCount = 112,
                    isVerified = true,
                    isFeatured = false,
                    isSaved = false,
                    latitude = 37.7610,
                    longitude = -122.4350,
                    specialOffer = "Complimentary 35-point safety inspection and tire pressure top-off with any oil change.",
                    tags = "EV Certified, Free WiFi Lounge, Loaner Bikes, Warranty Backed, Quick Turnaround",
                    categoryIconName = "auto",
                    accentColorHex = "#DC2626",
                    imageResName = "biz_auto"
                ),
                BusinessEntity(
                    id = 6,
                    name = "Heritage Sourdough Bakehouse",
                    category = "Cafes & Bakeries",
                    tagline = "Old-world stone-ground hearth breads & European morning pastries",
                    description = "Every loaf is naturally fermented for 36 hours using heirloom flours milled right in our open kitchen. Crispy golden crusts, airy crumb, cardamom buns, and seasonal fruit tarts that sell out early.",
                    address = "52 Market Lane",
                    neighborhood = "Old Town",
                    phone = "(555) 345-6789",
                    website = "https://heritagesourdough.com",
                    email = "orders@heritagesourdough.com",
                    hours = "Wed-Sun: 7:00 AM - 3:00 PM (or until sold out)",
                    isOpenNow = true,
                    priceLevel = "$$",
                    rating = 4.9f,
                    reviewCount = 95,
                    isVerified = true,
                    isFeatured = false,
                    isSaved = true,
                    latitude = 37.7845,
                    longitude = -122.4020,
                    specialOffer = "Complimentary sourdough pretzel with any bread loaf purchase on Thursdays!",
                    tags = "Stone Milled, Vegan Friendly, Curbside Pickup, Organic Flours",
                    categoryIconName = "bakery",
                    accentColorHex = "#B45309",
                    imageResName = "biz_bakery"
                ),
                BusinessEntity(
                    id = 7,
                    name = "Summit Trail Outfitters",
                    category = "Retail & Shopping",
                    tagline = "Gravel bikes, ultralight trekking packs & backcountry camping gear",
                    description = "Passionate outdoor enthusiasts providing community gear fittings, bike tuning, climbing gear resole drop-off, and topo maps. We host weekly group trail runs and bike repair clinics.",
                    address = "315 Ridge Crest Boulevard",
                    neighborhood = "West End",
                    phone = "(555) 567-8901",
                    website = "https://summittrailgear.com",
                    email = "adventures@summittrailgear.com",
                    hours = "Mon-Sat: 10:00 AM - 7:00 PM | Sun: 11:00 AM - 5:00 PM",
                    isOpenNow = true,
                    priceLevel = "$$$",
                    rating = 4.8f,
                    reviewCount = 47,
                    isVerified = true,
                    isFeatured = false,
                    isSaved = false,
                    latitude = 37.7580,
                    longitude = -122.4410,
                    specialOffer = "$25 store credit with purchase of any adult bicycle or complete pack setup.",
                    tags = "Bike Workshop, Gear Rental, Trail Community, Pet Friendly",
                    categoryIconName = "retail",
                    accentColorHex = "#059669",
                    imageResName = "biz_outdoors"
                ),
                BusinessEntity(
                    id = 8,
                    name = "Petals & Poetry Botanical",
                    category = "Retail & Shopping",
                    tagline = "Rare botanical houseplants, dried florals & ceramic pottery",
                    description = "An urban jungle filled with rare monsteras, trailing pothos, handmade terrariums, locally thrown ceramic pots, and custom floral arrangements for celebrations and everyday moments.",
                    address = "179 Bohemian Alley",
                    neighborhood = "Arts District",
                    phone = "(555) 987-6543",
                    website = "https://petalsandpoetry.com",
                    email = "bloom@petalsandpoetry.com",
                    hours = "Tue-Sat: 11:00 AM - 6:30 PM | Sun: 12:00 PM - 5:00 PM",
                    isOpenNow = false,
                    priceLevel = "$$",
                    rating = 4.9f,
                    reviewCount = 41,
                    isVerified = true,
                    isFeatured = false,
                    isSaved = false,
                    latitude = 37.7675,
                    longitude = -122.4180,
                    specialOffer = "Free mini hand-potted succulent with any botanical purchase above $35.",
                    tags = "Repotting Station, Workshops, Gift Wrapping, Local Delivery",
                    categoryIconName = "florist",
                    accentColorHex = "#16A34A",
                    imageResName = "biz_botanical"
                ),
                BusinessEntity(
                    id = 9,
                    name = "The Cozy Nook Books & Vinyl",
                    category = "Arts & Culture",
                    tagline = "Curated independent books, rare vintage vinyl & listening booths",
                    description = "Two floors of hand-selected literature, local author releases, first edition poetry, jazz and indie vinyl records with turntables for previewing. Cozy vintage armchairs and free drip tea.",
                    address = "604 Pine Street",
                    neighborhood = "Downtown",
                    phone = "(555) 876-5432",
                    website = "https://cozynookbooks.org",
                    email = "readers@cozynookbooks.org",
                    hours = "Mon-Sun: 10:00 AM - 8:00 PM",
                    isOpenNow = true,
                    priceLevel = "$",
                    rating = 4.9f,
                    reviewCount = 120,
                    isVerified = true,
                    isFeatured = true,
                    isSaved = true,
                    latitude = 37.7760,
                    longitude = -122.4170,
                    specialOffer = "Buy any 3 used books or vinyl records and receive a limited-edition tote bag free.",
                    tags = "Listening Stations, Community Book Club, Student Discount, Rare Editions",
                    categoryIconName = "books",
                    accentColorHex = "#4338CA",
                    imageResName = "biz_books"
                ),
                BusinessEntity(
                    id = 10,
                    name = "Urban Paw Veterinary & Grooming",
                    category = "Health & Wellness",
                    tagline = "Compassionate pet healthcare, low-stress gentle grooming & organic treats",
                    description = "Modern veterinary clinic designed specifically to keep animals calm and comfortable. Separate dog and cat waiting suites, preventative care plans, dental cleaning, and natural organic grooming.",
                    address = "920 Highland Drive",
                    neighborhood = "Northside",
                    phone = "(555) 765-4321",
                    website = "https://urbanpawcare.com",
                    email = "care@urbanpawcare.com",
                    hours = "Mon-Fri: 8:00 AM - 6:30 PM | Sat: 9:00 AM - 3:00 PM",
                    isOpenNow = true,
                    priceLevel = "$$",
                    rating = 4.8f,
                    reviewCount = 76,
                    isVerified = true,
                    isFeatured = false,
                    isSaved = false,
                    latitude = 37.7940,
                    longitude = -122.4110,
                    specialOffer = "20% off comprehensive preventative health exam for all newly registered pets.",
                    tags = "Fear-Free Certified, Cat-Only Suite, Online Booking, Emergency Triage",
                    categoryIconName = "pets",
                    accentColorHex = "#7C3AED",
                    imageResName = "biz_veterinary"
                ),
                BusinessEntity(
                    id = 11,
                    name = "Golden Crust Neapolitan Pizza",
                    category = "Restaurants",
                    tagline = "Blistered 900-degree sourdough pizzas, burrata & craft microbrews",
                    description = "Authentic Neapolitan pizza cooked in our imported volcanic stone oven in 90 seconds. House-stretched mozzarella, San Marzano tomato sauce, spicy hot honey pepperoni, and fresh basil.",
                    address = "388 Riverwalk Terrace",
                    neighborhood = "Riverside",
                    phone = "(555) 654-3210",
                    website = "https://goldencrustpizza.com",
                    email = "order@goldencrustpizza.com",
                    hours = "Daily: 12:00 PM - 10:30 PM",
                    isOpenNow = true,
                    priceLevel = "$$",
                    rating = 4.7f,
                    reviewCount = 134,
                    isVerified = true,
                    isFeatured = false,
                    isSaved = false,
                    latitude = 37.7800,
                    longitude = -122.4060,
                    specialOffer = "Tuesday special: Classic Margherita pizza + local draft beer for $16.",
                    tags = "Gluten-Free Crust Available, Heated Patio, Takeout & Delivery, Craft Beer Taps",
                    categoryIconName = "restaurant",
                    accentColorHex = "#EA580C",
                    imageResName = "biz_pizza"
                ),
                BusinessEntity(
                    id = 12,
                    name = "Prism Clay & Ceramics Studio",
                    category = "Arts & Culture",
                    tagline = "Pottery wheel workshops, clay open-studio & artisanal tableware",
                    description = "A warm, sunlit ceramic workshop offering beginner pottery wheel classes, hand-building nights with wine, private team events, and a retail showcase of pottery by local ceramic artists.",
                    address = "120 Factory Yard, Bay 2",
                    neighborhood = "Arts District",
                    phone = "(555) 543-2109",
                    website = "https://prismclaystudio.com",
                    email = "workshops@prismclaystudio.com",
                    hours = "Wed-Sun: 1:00 PM - 9:00 PM",
                    isOpenNow = true,
                    priceLevel = "$$$",
                    rating = 5.0f,
                    reviewCount = 29,
                    isVerified = true,
                    isFeatured = false,
                    isSaved = false,
                    latitude = 37.7660,
                    longitude = -122.4215,
                    specialOffer = "15% discount on 2-person Clay & Sip weekend introductory workshops.",
                    tags = "Beginner Friendly, Wheel & Handbuilding, Kiln Firing Service, Gift Cards",
                    categoryIconName = "palette",
                    accentColorHex = "#D97706",
                    imageResName = "biz_ceramics"
                )
            )

            businessDao.insertAll(initialBusinesses)

            val initialReviews = listOf(
                ReviewEntity(
                    businessId = 1,
                    authorName = "Elena Rostova",
                    rating = 5,
                    comment = "Hands down the best flat white and almond croissant in the city. The baristas genuinely care about the craft, and the natural light upstairs makes it a dream for morning remote work.",
                    dateString = "2 days ago",
                    helpfulCount = 14
                ),
                ReviewEntity(
                    businessId = 1,
                    authorName = "Marcus Chen",
                    rating = 5,
                    comment = "The Ethiopian single-origin pour over was phenomenal! Great playlist, fast wifi, and very friendly service. Will definitely be a regular here.",
                    dateString = "1 week ago",
                    helpfulCount = 8
                ),
                ReviewEntity(
                    businessId = 2,
                    authorName = "Sarah Jenkins",
                    rating = 5,
                    comment = "The seafood risotto and wild halibut were extraordinary. The patio overlooking the river sunset is unbeatable for a date night or anniversary dinner.",
                    dateString = "3 days ago",
                    helpfulCount = 11
                ),
                ReviewEntity(
                    businessId = 3,
                    authorName = "David Miller",
                    rating = 5,
                    comment = "We had a custom black walnut dining table made for our new home. The craftsmanship and attention to detail surpassed our highest expectations. Truly heirloom quality!",
                    dateString = "2 weeks ago",
                    helpfulCount = 9
                ),
                ReviewEntity(
                    businessId = 4,
                    authorName = "Chloe Ramirez",
                    rating = 5,
                    comment = "The sound bath and infrared sauna session melted all my work stress away. The studio feels like a temple of calmness in the middle of town.",
                    dateString = "Yesterday",
                    helpfulCount = 5
                ),
                ReviewEntity(
                    businessId = 6,
                    authorName = "Arthur Pendelton",
                    rating = 5,
                    comment = "Get here right when they open at 7 AM for the warm cardamom buns! The seeded country sourdough has the perfect tang and crust.",
                    dateString = "4 days ago",
                    helpfulCount = 17
                )
            )

            reviewDao.insertAll(initialReviews)

            promotionDao?.let { pDao ->
                if (pDao.getPromotionCount() == 0 || force) {
                    val initialPromotions = listOf(
                        PromotionEntity(
                            businessId = 5,
                            businessName = "Artisan Roasters & Brews",
                            category = "Cafes & Bakeries",
                            title = "Complimentary Cold Brew or Drip with Any Pastry",
                            discountDescription = "FREE DRIP COFFEE",
                            promoCode = "MORNINGBREW",
                            terms = "Valid weekdays before 10 AM. Mention code to barista.",
                            validUntil = "Ongoing Community Offer",
                            imageResName = "biz_cafe",
                            postedByOwnerEmail = "owner@townspot.com"
                        ),
                        PromotionEntity(
                            businessId = 1,
                            businessName = "Elite Edge Gymnasium",
                            category = "Gymnasium",
                            title = "50% Off First Month Membership + Free Body Scan",
                            discountDescription = "50% OFF",
                            promoCode = "ELITE50",
                            terms = "Applies to new member enrollments. Show coupon at front desk.",
                            validUntil = "Valid through this month",
                            imageResName = "biz_gymnasium",
                            postedByOwnerEmail = "james@eliteedgegym.com"
                        ),
                        PromotionEntity(
                            businessId = 4,
                            businessName = "Zenith Mind & Body Yoga",
                            category = "Wellness",
                            title = "New Student Discovery Pass: 7 Days Unlimited",
                            discountDescription = "$29 PASS",
                            promoCode = "ZEN7DAYS",
                            terms = "Includes all vinyasa, yin, and sound bath sessions.",
                            validUntil = "Limited seasonal passes available",
                            imageResName = "biz_wellness",
                            postedByOwnerEmail = "contact@zenithyoga.com"
                        ),
                        PromotionEntity(
                            businessId = 2,
                            businessName = "Riverfront Bistro & Raw Bar",
                            category = "Restaurant",
                            title = "Weekday Chef's Special: 20% Off Artisan Entrees",
                            discountDescription = "20% OFF",
                            promoCode = "RIVER20",
                            terms = "Available Tuesday through Thursday dinner service.",
                            validUntil = "Valid through end of season",
                            imageResName = "biz_bistro",
                            postedByOwnerEmail = "info@riverfrontbistro.com"
                        ),
                        PromotionEntity(
                            businessId = 3,
                            businessName = "Timber & Forge Woodcraft",
                            category = "Home Services",
                            title = "Free In-Home Design Consultation & 10% Off First Table",
                            discountDescription = "10% OFF",
                            promoCode = "FORGE10",
                            terms = "Applies to custom handcrafted dining and coffee tables.",
                            validUntil = "Ongoing TownSpot Resident Special",
                            imageResName = "biz_woodcraft",
                            postedByOwnerEmail = "hello@timberforge.com"
                        )
                    )
                    pDao.insertAll(initialPromotions)
                }
            }
        }
    }
}
