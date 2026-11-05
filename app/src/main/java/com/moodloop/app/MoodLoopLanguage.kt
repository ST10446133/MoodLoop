package com.moodloop.app

import android.content.Context

/** Local copy for app-owned interface text. User-entered journal and mood text is never translated. */
object MoodLoopLanguage {
    private val isiZulu = mapOf(
        "Home" to "Ikhaya", "Log" to "Bhala", "Journal" to "Ijenali", "Wall" to "Udonga", "Me" to "Mina",
        "Overview" to "Uhlolojikelele", "Tracker" to "Umlandeleli", "Badges" to "Amabheji", "Settings" to "Izilungiselelo",
        "Welcome back" to "Siyakwamukela futhi", "Sign in to continue your reflection journey." to "Ngena ukuze uqhubeke nohambo lwakho lokuzindla.",
        "Sign in" to "Ngena ngemvume", "Register" to "Bhalisa", "Create account" to "Dala i-akhawunti", "Your name" to "Igama lakho",
        "Email address" to "Ikheli le-imeyili", "Password" to "Iphasiwedi", "Confirm password" to "Qinisekisa iphasiwedi", "Show" to "Bonisa", "Hide" to "Fihla",
        "Forgot password?" to "Ukhohlwe iphasiwedi?", "Continue with Google" to "Qhubeka nge-Google", "Already have an account? " to "Usunayo i-akhawunti? ",
        "Don't have an account? " to "Awunayo i-akhawunti? ", "Get started" to "Qalisa", "I already have an account" to "Sengivele nginayo i-akhawunti",
        "A calm, anonymous space for student reflection.\nReflect. Connect. Heal." to "Indawo ezolile nengaziwa yabafundi yokuzindla.\nZindla. Xhumana. Philisa.",
        "Log your mood" to "Bhala indlela ozizwa ngayo", "How are you feeling right now?\nDate and time recorded automatically." to "Uzizwa kanjani manje?\nUsuku nesikhathi kugcinwa ngokuzenzakalelayo.",
        "CHOOSE AN EMOTION" to "KHETHA UMUZWA", "ADD A NOTE (optional)" to "ENGEZA INOTHI (akuphoqelekile)",
        "Write about what made you feel this way..." to "Bhala ngalokho okukwenze wazizwa kanje...", "Save mood · +10 pts" to "Gcina umuzwa · +10 amaphuzu",
        "Points you earn" to "Amaphuzu owatholayo", "Log a mood" to "Bhala umuzwa", "Happy" to "Ojabule", "Calm" to "Ozolile", "Excited" to "Ojabule kakhulu",
        "Sad" to "Odabukile", "Angry" to "Othukuthele", "Anxious" to "Okhathazekile", "Stressed" to "Ocindezelekile", "Tired" to "Okhathele", "Neutral" to "Ovamile",
        "New reflection" to "Ukuzindla okusha", "YOUR REFLECTION" to "UKUZINDLA KWAKHO", "Write about what has been on your mind..." to "Bhala okukukhathazayo...",
        "EMOTION" to "UMUZWA", "VISIBILITY" to "UKUBONAKALA", "Your name and username will never appear on the Mood Wall." to "Igama lakho ngeke livele odongeni lwemizwa.",
        "Save reflection · +15 pts" to "Gcina ukuzindla · +15 amaphuzu", "Settings" to "Izilungiselelo", "ACCOUNT" to "I-AKHAWUNTI", "PREFERENCES" to "OKUNCANYELWAYO",
        "Manage your MoodLoop" to "Phatha i-MoodLoop yakho", "Account, password, and reminder controls live here." to "Izilawuli ze-akhawunti, iphasiwedi nezikhumbuzi zilapha.",
        "Edit display name" to "Hlela igama eliboniswayo", "Change how your name appears in the app." to "Shintsha indlela igama lakho elivela ngayo kuhlelo lokusebenza.",
        "Change password" to "Shintsha iphasiwedi", "Update your Firebase sign-in password." to "Buyekeza iphasiwedi yakho yokungena ye-Firebase.",
        "Reminder settings" to "Izilungiselelo zezikhumbuzi", "Choose when MoodLoop should remind you." to "Khetha ukuthi i-MoodLoop ikukhumbuze nini.",
        "Language" to "Ulimi", "Choose the language used in Settings." to "Khetha ulimi olusetshenziswa kokuthi Izilungiselelo.",
        "Log out" to "Phuma", "Leave this account on this device." to "Susa le akhawunti kule divayisi.", "Delete account" to "Susa i-akhawunti",
        "Permanently remove this account from MoodLoop." to "Susa le akhawunti ku-MoodLoop unomphela.", "Choose language" to "Khetha ulimi", "Cancel" to "Khansela",
        "Connecting to the internet..." to "Ixhuma ku-inthanethi...", "No internet connection" to "Ayikho inthanethi",
        "Check your internet connection and try again." to "Hlola uxhumano lwakho lwe-inthanethi bese uzama futhi.", "OK" to "KULUNGILE",
        "LAST 7 DAYS" to "IZINSUKU EZIYISI-7 EZEDLULE", "Mood entries logged" to "Imizwa ebhaliwe", "Your trend will appear after you log moods." to "Umkhuba wakho uzovela uma usubhale imizwa.",
        "Your recent check-ins are shown below." to "Ukuhlola kwakho kwakamuva kuboniswe ngezansi.", "Mood entries" to "Imizwa ebhaliwe", "Achievements" to "Izimpumelelo",
        "Current streak" to "Uchungechunge lwamanje", "Best streak" to "Uchungechunge olude kunawo wonke", "Most logged emotion" to "Umuzwa obhalwe kakhulu",
        "EARNED BADGES" to "AMABHEJI ATHOLIWE", "No badges yet" to "Awekho amabheji okwamanje", "Achievements unlocked" to "Izimpumelelo zivuliwe",
        "Keep checking in to collect more progress badges." to "Qhubeka uhlola ukuze uthole amabheji enqubekelaphambili.", "QUICK ACCESS" to "UKUFINYELELA OKUSHESHAYO", "LATEST MOOD" to "UMUZWA WAKAMUVA",
        "Enable supportive reminders" to "Vumela izikhumbuzi ezisekelayo", "PREFERRED TIME" to "ISIKHATHI OSIKHETHAYO", "REMINDER" to "ISIKHUMBUZI",
        "Use 24-hour time, for example 18:30." to "Sebenzisa isikhathi samahora angu-24, isibonelo 18:30.", "Save reminder settings" to "Gcina izilungiselelo zesikhumbuzi",
        "Gentle check-in reminders" to "Izikhumbuzi ezithambile zokuzihlola", "Pick a time that feels calm and realistic for you." to "Khetha isikhathi esikufanele nesingokoqobo.",
        "DISPLAY NAME" to "IGAMA ELIBONISWAYO", "Enter your display name" to "Faka igama lakho eliboniswayo", "Save display name" to "Gcina igama eliboniswayo",
        "Choose how MoodLoop greets you" to "Khetha ukuthi i-MoodLoop ikubingelela kanjani", "This name is only used inside your private app experience." to "Leli gama lisetshenziswa ngaphakathi kohlelo lwakho kuphela.",
        "PASSWORD DETAILS" to "IMINININGWANE YE-IPHASIWEDI", "Current password" to "Iphasiwedi yamanje", "New password" to "Iphasiwedi entsha", "Confirm new password" to "Qinisekisa iphasiwedi entsha",
        "Save password" to "Gcina iphasiwedi", "Keep your account protected" to "Vikela i-akhawunti yakho", "Use a password you do not use anywhere else." to "Sebenzisa iphasiwedi ongayisebenzisi kwenye indawo.",
        "DELETE ACCOUNT" to "SUSA I-AKHAWUNTI", "Delete my account" to "Susa i-akhawunti yami", "Remove your MoodLoop account" to "Susa i-akhawunti yakho ye-MoodLoop",
        "This action cannot be undone once it succeeds." to "Lesi senzo ngeke sihlehliswe uma sesiphumelele.", "Save" to "Gcina", "Back" to "Emuva", "Share anonymously" to "Yabelana ngokungaziwa",
        "Private" to "Okuyimfihlo", "Shared" to "Kwabiwe", "Today" to "Namuhla", "No entries yet" to "Akukho okubhaliwe okwamanje", "No mood yet" to "Awukho umuzwa okwamanje",
        "STREAK" to "UCHUNGECHUNGE", "POINTS" to "AMAPHUZU", "LEVEL" to "IZINGA", "total" to "esewonke",
        "✏ New" to "✏ Okusha", "🔒 Private" to "🔒 Okuyimfihlo", "🌐 Share anonymously" to "🌐 Yabelana ngokungaziwa",
        "All" to "Konke", "Anonymous" to "Ongaziwa", "💛 This helped me" to "💛 Lokhu kungisizile", "💜 Helped" to "💜 Kusizile",
        "First mood" to "Umuzwa wokuqala", "First reflection" to "Ukuzindla kokuqala", "Shared support" to "Ukwesekana ngokwabelana",
        "Someone found your reflection helpful." to "Omunye uthole ukuzindla kwakho kuwusizo.",
        "You earned a new MoodLoop badge." to "Uthole ibheji elisha le-MoodLoop.",
        "You reached a new MoodLoop level." to "Ufinyelele ezingeni elisha le-MoodLoop.",
        "Log a mood to start tracking." to "Bhala umuzwa ukuze uqale ukulandelela.", "Write\nreflection" to "Bhala\nukuzindla", "My profile" to "Iphrofayela yami",
        "Your activity" to "Umsebenzi wakho", "Your progress at a glance" to "Inqubekelaphambili yakho ngamafuphi",
        "A gentle look at your mood check-ins." to "Ukubheka ngobumnene ukuhlolwa kwemizwa yakho.",
        "Celebrate the progress you have made." to "Gubha inqubekelaphambili osuyenzile.",
        "Your badges will appear after moods, reflections, streaks, and sharing." to "Amabheji akho azovela ngemva kwemizwa, ukuzindla, uchungechunge nokwabelana.",
        "A calm place to manage your account." to "Indawo ezolile yokuphatha i-akhawunti yakho.",
        "QUICK ACCESS" to "UKUFINYELELA OKUSHESHAYO", "LATEST MOOD" to "UMUZWA WAKAMUVA"
        ,"Start your private reflection journey today." to "Qala uhambo lwakho oluyimfihlo lokuzindla namuhla.",
        "New" to "Okusha", "No reflections yet" to "Akukho ukuzindla okwamanje", "Write your first reflection" to "Bhala ukuzindla kwakho kokuqala",
        "Writing about how you feel can be powerful. Your\nreflections are private unless you choose to share." to "Ukubhala ngemizwa yakho kungasiza.\nUkuzindla kwakho kuyimfihlo ngaphandle kokuthi wabelane.",
        "🌐 shared" to "🌐 kwabiwe", "Mood Wall" to "Udonga Lwemizwa", "Anonymous reflections — no profiles, no public likes." to "Ukuzindla okungaziwa — awekho amaphrofayela noma ukuthandwa komphakathi.",
        "Your feelings are valid, always." to "Imizwa yakho ibalulekile njalo.", "Most logged emotion" to "Umuzwa obhalwe kakhulu", "times" to "izikhathi",
        "Reflection" to "Ukuzindla", "MOODLOOP" to "MOODLOOP", "Earned badges" to "Amabheji atholiwe",
        "Mood Wall" to "Udonga Lwemizwa", "Anonymous reflections — no profiles, no public likes." to "Ukuzindla okungaziwa — awekho amaphrofayela noma ukuthandwa komphakathi.",
        "No reflections yet" to "Akukho ukuzindla okwamanje", "Writing about how you feel can be powerful. Your\nreflections are private unless you choose to share." to "Ukubhala ngemizwa yakho kungasiza.\nUkuzindla kwakho kuyimfihlo ngaphandle kokuthi wabelane.",
        "Write your first reflection" to "Bhala ukuzindla kwakho kokuqala", "Mood entries logged" to "Imizwa ebhaliwe",
        "Your feelings are valid, always." to "Imizwa yakho ibalulekile njalo.", "CURRENT PASSWORD" to "IPHASIWEDI YAMANJE", "NEW PASSWORD" to "IPHASIWEDI ENTSHA",
        "CONFIRM NEW PASSWORD" to "QINISEKISA IPHASIWEDI ENTSHA", "Share anonymously to Mood Wall" to "Yabelana ngokungaziwa odongeni lwemizwa",
        "Earned" to "Kutholakele", "Choose your emotion" to "Khetha umuzwa wakho", "Save reminder settings" to "Gcina izilungiselelo zesikhumbuzi",
        "Passwords do not match" to "Amaphasiwedi awafani", "Account created successfully" to "I-akhawunti idalwe ngempumelelo", "Signed in successfully" to "Ungene ngempumelelo",
        "Mood saved successfully" to "Umuzwa ugcinwe ngempumelelo", "Reflection cannot be empty" to "Ukuzindla akukwazi ukungabi nalutho", "Please choose an emotion" to "Sicela ukhethe umuzwa",
        "Saved offline. It will sync when connected" to "Kugcinwe ngaphandle kwe-inthanethi. Kuzovumelaniswa uma usuxhumekile.",
        "Please choose private or share anonymously" to "Sicela ukhethe okuyimfihlo noma ukwabelana ngokungaziwa", "Reflection saved" to "Ukuzindla kugcinwe",
        "Reflection deleted" to "Ukuzindla kususiwe", "Account deleted" to "I-akhawunti isusiwe", "Display name cannot be empty" to "Igama eliboniswayo alikwazi ukungabi nalutho",
        "Edit reflection" to "Hlela ukuzindla", "Please complete all required fields" to "Sicela ugcwalise zonke izinkambu ezidingekayo", "Please enter your email and password" to "Sicela ufake i-imeyili nephasiwedi yakho",
        "Login failed. Check your details and try again." to "Ukungena kuhlulekile. Hlola imininingwane yakho bese uzama futhi.", "Account doesn't exist" to "I-akhawunti ayikho",
        "Incorrect password" to "Iphasiwedi ayilungile", "Incorrect email or password" to "I-imeyili noma iphasiwedi ayilungile",
        "This account has been disabled" to "Le akhawunti ivaliwe", "Too many sign-in attempts. Try again later." to "Imizamo eminingi yokungena. Zama futhi kamuva.",
        "Sign-in failed. Please try again." to "Ukungena kuhlulekile. Sicela uzame futhi.",
        "Please enter your email address first" to "Sicela uqale ufake ikheli lakho le-imeyili", "Please enter a valid email address" to "Sicela ufake ikheli le-imeyili elivumelekile",
        "Password reset email sent" to "I-imeyili yokusetha kabusha iphasiwedi ithunyelwe",
        "Please enter your current password" to "Sicela ufake iphasiwedi yakho yamanje",
        "Confirm account deletion" to "Qinisekisa ukususwa kwe-akhawunti", "Enter your current password to delete this account." to "Faka iphasiwedi yakho yamanje ukuze ususe le akhawunti.",
        "Enter your current password to confirm account deletion" to "Faka iphasiwedi yakho yamanje ukuze uqinisekise ukususwa kwe-akhawunti",
        "Sign in with Google again to confirm account deletion" to "Ngena nge-Google futhi ukuze uqinisekise ukususwa kwe-akhawunti",
        "Reauthentication failed. Check your credentials and try again." to "Ukuqinisekisa kabusha kuhlulekile. Hlola imininingwane yakho bese uzama futhi.",
        "Account deletion was cancelled" to "Ukususwa kwe-akhawunti kukhanseliwe",
        "Account data could not be deleted. Please try again." to "Idatha ye-akhawunti ayikwazanga ukususwa. Sicela uzame futhi.",
        "Reauthentication is not available for this sign-in method" to "Ukuqinisekisa kabusha akutholakali ngale ndlela yokungena",
        "Please enter a new password" to "Sicela ufake iphasiwedi entsha", "Please confirm your new password" to "Sicela uqinisekise iphasiwedi entsha",
        "New passwords do not match" to "Amaphasiwedi amasha awafani", "Password must be at least 6 characters" to "Iphasiwedi kufanele ibe nezinhlamvu okungenani eziyisi-6",
        "Password changed successfully" to "Iphasiwedi ishintshwe ngempumelelo", "Could not change password" to "Ayikwazanga ukushintsha iphasiwedi",
        "Current password is incorrect" to "Iphasiwedi yamanje ayilungile", "Display name cannot be empty" to "Igama eliboniswayo alikwazi ukungabi nalutho",
        "Profile updated" to "Iphrofayela ibuyekeziwe", "Please enter a valid time, like 18:30" to "Sicela ufake isikhathi esivumelekile, njengo-18:30",
        "Delete reflection?" to "Susa ukuzindla?", "This cannot be undone." to "Lokhu ngeke kuhlehliswe.", "Confirm" to "Qinisekisa"
    )

    private val afrikaans = mapOf(
        "Home" to "Tuis", "Log" to "Aanteken", "Journal" to "Joernaal", "Wall" to "Muur", "Me" to "Ek",
        "Overview" to "Oorsig", "Tracker" to "Nasporing", "Badges" to "Kentekens", "Settings" to "Instellings",
        "Welcome back" to "Welkom terug", "Sign in to continue your reflection journey." to "Meld aan om met jou refleksiereis voort te gaan.",
        "Sign in" to "Meld aan", "Register" to "Registreer", "Create account" to "Skep rekening", "Your name" to "Jou naam",
        "Email address" to "E-posadres", "Password" to "Wagwoord", "Confirm password" to "Bevestig wagwoord", "Show" to "Wys", "Hide" to "Versteek",
        "Forgot password?" to "Wagwoord vergeet?", "Continue with Google" to "Gaan voort met Google", "Already have an account? " to "Het jy reeds ’n rekening? ",
        "Don't have an account? " to "Het jy nie ’n rekening nie? ", "Get started" to "Begin", "I already have an account" to "Ek het reeds ’n rekening",
        "A calm, anonymous space for student reflection.\nReflect. Connect. Heal." to "’n Rustige, anonieme ruimte vir studente-refleksie.\nDink na. Verbind. Groei.",
        "Log your mood" to "Teken jou bui aan", "How are you feeling right now?\nDate and time recorded automatically." to "Hoe voel jy nou?\nDatum en tyd word outomaties aangeteken.",
        "CHOOSE AN EMOTION" to "KIES ’N EMOSIE", "ADD A NOTE (optional)" to "VOEG ’N NOTA BY (opsioneel)",
        "Write about what made you feel this way..." to "Skryf oor wat jou so laat voel het...", "Save mood · +10 pts" to "Stoor bui · +10 punte",
        "Points you earn" to "Punte wat jy verdien", "Log a mood" to "Teken ’n bui aan", "Happy" to "Gelukkig", "Calm" to "Kalm", "Excited" to "Opgewonde",
        "Sad" to "Hartseer", "Angry" to "Kwaad", "Anxious" to "Angstig", "Stressed" to "Gestres", "Tired" to "Moeg", "Neutral" to "Neutraal",
        "New reflection" to "Nuwe refleksie", "YOUR REFLECTION" to "JOU REFLEKSIE", "Write about what has been on your mind..." to "Skryf oor wat jou kwel...",
        "EMOTION" to "EMOSIE", "VISIBILITY" to "SIGBAARHEID", "Your name and username will never appear on the Mood Wall." to "Jou naam en gebruikersnaam verskyn nooit op die Buiemuur nie.",
        "Save reflection · +15 pts" to "Stoor refleksie · +15 punte", "ACCOUNT" to "REKENING", "PREFERENCES" to "VOORKEURE",
        "Manage your MoodLoop" to "Bestuur jou MoodLoop", "Account, password, and reminder controls live here." to "Rekening-, wagwoord- en herinneringbeheer is hier.",
        "Edit display name" to "Wysig vertoonnaam", "Change how your name appears in the app." to "Verander hoe jou naam in die toepassing verskyn.",
        "Change password" to "Verander wagwoord", "Update your Firebase sign-in password." to "Dateer jou Firebase-aanmeldwagwoord op.",
        "Reminder settings" to "Herinneringinstellings", "Choose when MoodLoop should remind you." to "Kies wanneer MoodLoop jou moet herinner.",
        "Language" to "Taal", "Choose the language used in Settings." to "Kies die taal wat in Instellings gebruik word.",
        "Log out" to "Teken af", "Leave this account on this device." to "Verwyder hierdie rekening van hierdie toestel.", "Delete account" to "Verwyder rekening",
        "Permanently remove this account from MoodLoop." to "Verwyder hierdie rekening permanent uit MoodLoop.", "Choose language" to "Kies taal", "Cancel" to "Kanselleer",
        "Connecting to the internet..." to "Koppel aan die internet...", "No internet connection" to "Geen internetverbinding nie",
        "Check your internet connection and try again." to "Gaan jou internetverbinding na en probeer weer.", "OK" to "Goed",
        "LAST 7 DAYS" to "LAASTE 7 DAE", "Mood entries logged" to "Bui-inskrywings", "Your trend will appear after you log moods." to "Jou tendens verskyn nadat jy buie aanteken.",
        "Your recent check-ins are shown below." to "Jou jongste aanmeldings verskyn hieronder.", "Mood entries" to "Bui-inskrywings", "Achievements" to "Prestasies",
        "Current streak" to "Huidige reeks", "Best streak" to "Beste reeks", "Most logged emotion" to "Mees aangetekende emosie",
        "EARNED BADGES" to "VERDIENDE KENTEKENS", "No badges yet" to "Nog geen kentekens nie", "Achievements unlocked" to "Prestasies ontsluit",
        "Keep checking in to collect more progress badges." to "Meld gereeld aan om nog vorderingskentekens te kry.", "QUICK ACCESS" to "VINNIGE TOEGANG", "LATEST MOOD" to "JONGSTE BUİ",
        "Enable supportive reminders" to "Aktiveer ondersteunende herinnerings", "PREFERRED TIME" to "VOORKEURTYD", "REMINDER" to "HERINNERING",
        "Use 24-hour time, for example 18:30." to "Gebruik 24-uurtyd, byvoorbeeld 18:30.", "Save reminder settings" to "Stoor herinneringinstellings",
        "Gentle check-in reminders" to "Sagte aanmeldherinnerings", "Pick a time that feels calm and realistic for you." to "Kies ’n tyd wat rustig en haalbaar voel.",
        "DISPLAY NAME" to "VERTOONNAAM", "Enter your display name" to "Voer jou vertoonnaam in", "Save display name" to "Stoor vertoonnaam",
        "Choose how MoodLoop greets you" to "Kies hoe MoodLoop jou groet", "This name is only used inside your private app experience." to "Hierdie naam word net binne jou private toepassing gebruik.",
        "PASSWORD DETAILS" to "WAGWOORDBESONDERHEDE", "Current password" to "Huidige wagwoord", "New password" to "Nuwe wagwoord", "Confirm new password" to "Bevestig nuwe wagwoord",
        "Save password" to "Stoor wagwoord", "Keep your account protected" to "Hou jou rekening veilig", "Use a password you do not use anywhere else." to "Gebruik ’n wagwoord wat jy nêrens anders gebruik nie.",
        "DELETE ACCOUNT" to "VERWYDER REKENING", "Delete my account" to "Verwyder my rekening", "Remove your MoodLoop account" to "Verwyder jou MoodLoop-rekening",
        "This action cannot be undone once it succeeds." to "Hierdie aksie kan nie ongedaan gemaak word nie.", "Save" to "Stoor", "Back" to "Terug", "Share anonymously" to "Deel anoniem",
        "Private" to "Privaat", "Shared" to "Gedeel", "Today" to "Vandag", "No entries yet" to "Nog geen inskrywings nie", "No mood yet" to "Nog geen bui nie",
        "STREAK" to "REEKS", "POINTS" to "PUNTE", "LEVEL" to "VLAK", "total" to "totaal",
        "✏ New" to "✏ Nuut", "🔒 Private" to "🔒 Privaat", "🌐 Share anonymously" to "🌐 Deel anoniem",
        "All" to "Alles", "Anonymous" to "Anoniem", "💛 This helped me" to "💛 Dit het my gehelp", "💜 Helped" to "💜 Gehelp",
        "First mood" to "Eerste bui", "First reflection" to "Eerste refleksie", "Shared support" to "Gedeelde ondersteuning",
        "Someone found your reflection helpful." to "Iemand het jou refleksie nuttig gevind.",
        "You earned a new MoodLoop badge." to "Jy het ’n nuwe MoodLoop-kenteken verdien.",
        "You reached a new MoodLoop level." to "Jy het ’n nuwe MoodLoop-vlak bereik.",
        "Log a mood to start tracking." to "Teken ’n bui aan om nasporing te begin.", "Write\nreflection" to "Skryf\nrefleksie", "My profile" to "My profiel",
        "Your activity" to "Jou aktiwiteit", "Your progress at a glance" to "Jou vordering in ’n oogopslag",
        "A gentle look at your mood check-ins." to "’n Rustige oorsig van jou bui-inskrywings.",
        "Celebrate the progress you have made." to "Vier die vordering wat jy gemaak het.",
        "Your badges will appear after moods, reflections, streaks, and sharing." to "Jou kentekens verskyn ná buie, refleksies, reekse en deling.",
        "A calm place to manage your account." to "’n Rustige plek om jou rekening te bestuur.",
        "QUICK ACCESS" to "VINNIGE TOEGANG", "LATEST MOOD" to "JONGSTE BUİ"
        ,"Start your private reflection journey today." to "Begin vandag jou private refleksiereis.",
        "New" to "Nuut", "No reflections yet" to "Nog geen refleksies nie", "Write your first reflection" to "Skryf jou eerste refleksie",
        "Writing about how you feel can be powerful. Your\nreflections are private unless you choose to share." to "Dit kan help om oor jou gevoelens te skryf.\nJou refleksies bly privaat tensy jy dit deel.",
        "🌐 shared" to "🌐 gedeel", "Mood Wall" to "Buiemuur", "Anonymous reflections — no profiles, no public likes." to "Anonieme refleksies — geen profiele of openbare laaiks nie.",
        "Your feelings are valid, always." to "Jou gevoelens is altyd geldig.", "Most logged emotion" to "Mees aangetekende emosie", "times" to "keer",
        "Reflection" to "Refleksie", "MOODLOOP" to "MOODLOOP", "Earned badges" to "Kentekens verdien",
        "Mood Wall" to "Buiemuur", "Anonymous reflections — no profiles, no public likes." to "Anonieme refleksies — geen profiele of openbare laaiks nie.",
        "No reflections yet" to "Nog geen refleksies nie", "Writing about how you feel can be powerful. Your\nreflections are private unless you choose to share." to "Dit kan help om oor jou gevoelens te skryf.\nJou refleksies bly privaat tensy jy dit deel.",
        "Write your first reflection" to "Skryf jou eerste refleksie", "Your feelings are valid, always." to "Jou gevoelens is altyd geldig.",
        "CURRENT PASSWORD" to "HUIDIGE WAGWOORD", "NEW PASSWORD" to "NUWE WAGWOORD", "CONFIRM NEW PASSWORD" to "BEVESTIG NUWE WAGWOORD",
        "Share anonymously to Mood Wall" to "Deel anoniem op die Buiemuur", "Passwords do not match" to "Wagwoorde stem nie ooreen nie",
        "Account created successfully" to "Rekening suksesvol geskep", "Signed in successfully" to "Suksesvol aangemeld", "Mood saved successfully" to "Bui suksesvol gestoor",
        "Saved offline. It will sync when connected" to "Vanlyn gestoor. Dit sal sinkroniseer wanneer jy weer verbind is.",
        "Reflection cannot be empty" to "Refleksie kan nie leeg wees nie", "Please choose an emotion" to "Kies asseblief ’n emosie",
        "Please choose private or share anonymously" to "Kies asseblief privaat of deel anoniem", "Reflection saved" to "Refleksie gestoor",
        "Reflection deleted" to "Refleksie verwyder", "Account deleted" to "Rekening verwyder", "Display name cannot be empty" to "Vertoonnaam kan nie leeg wees nie",
        "Edit reflection" to "Wysig refleksie", "Please complete all required fields" to "Voltooi asseblief al die vereiste velde", "Please enter your email and password" to "Voer asseblief jou e-posadres en wagwoord in",
        "Login failed. Check your details and try again." to "Aanmelding het misluk. Gaan jou besonderhede na en probeer weer.", "Account doesn't exist" to "Rekening bestaan nie",
        "Incorrect password" to "Verkeerde wagwoord", "Incorrect email or password" to "Verkeerde e-posadres of wagwoord",
        "This account has been disabled" to "Hierdie rekening is gedeaktiveer", "Too many sign-in attempts. Try again later." to "Te veel aanmeldpogings. Probeer later weer.",
        "Sign-in failed. Please try again." to "Aanmelding het misluk. Probeer asseblief weer.",
        "Please enter your email address first" to "Voer eers jou e-posadres in", "Please enter a valid email address" to "Voer ’n geldige e-posadres in",
        "Password reset email sent" to "Wagwoordterugstelling-e-pos gestuur",
        "Please enter your current password" to "Voer jou huidige wagwoord in",
        "Confirm account deletion" to "Rekeningverwydering bevestig", "Enter your current password to delete this account." to "Voer jou huidige wagwoord in om hierdie rekening te verwyder.",
        "Enter your current password to confirm account deletion" to "Voer jou huidige wagwoord in om rekeningverwydering te bevestig",
        "Sign in with Google again to confirm account deletion" to "Meld weer met Google aan om rekeningverwydering te bevestig",
        "Reauthentication failed. Check your credentials and try again." to "Herverifikasie het misluk. Gaan jou besonderhede na en probeer weer.",
        "Account deletion was cancelled" to "Rekeningverwydering is gekanselleer",
        "Account data could not be deleted. Please try again." to "Rekeningdata kon nie verwyder word nie. Probeer asseblief weer.",
        "Reauthentication is not available for this sign-in method" to "Herverifikasie is nie vir hierdie aanmeldmetode beskikbaar nie",
        "Please enter a new password" to "Voer ’n nuwe wagwoord in", "Please confirm your new password" to "Bevestig jou nuwe wagwoord",
        "New passwords do not match" to "Nuwe wagwoorde stem nie ooreen nie", "Password must be at least 6 characters" to "Wagwoord moet minstens 6 karakters bevat",
        "Password changed successfully" to "Wagwoord suksesvol verander", "Could not change password" to "Kon nie wagwoord verander nie",
        "Current password is incorrect" to "Huidige wagwoord is verkeerd",
        "Profile updated" to "Profiel opgedateer", "Please enter a valid time, like 18:30" to "Voer asseblief ’n geldige tyd in, soos 18:30",
        "Delete reflection?" to "Verwyder refleksie?", "This cannot be undone." to "Dit kan nie ongedaan gemaak word nie.", "Confirm" to "Bevestig"
    )

    fun translate(context: Context, value: String): String {
        val language = context.getSharedPreferences("moodloop_settings", Context.MODE_PRIVATE).getString("language", "en")
        val translated = when (language) {
            "zu" -> isiZulu[value] ?: value
            "af" -> afrikaans[value] ?: value
            else -> value
        }
        if (translated != value) return translated
        val moodLabel = Regex("^(\\S+\\s+)(Happy|Calm|Excited|Sad|Angry|Anxious|Stressed|Tired|Neutral)$").matchEntire(value)
        if (moodLabel != null && (language == "zu" || language == "af")) {
            val dictionary = if (language == "zu") isiZulu else afrikaans
            val moodTranslation = dictionary[moodLabel.groupValues[2]]
            if (moodTranslation != null) return moodLabel.groupValues[1] + moodTranslation
        }
        val weekday = Regex("^(Mon|Tue|Wed|Thu|Fri|Sat|Sun)(\\s+\\d{1,2})?$").matchEntire(value)
        if (weekday != null && (language == "zu" || language == "af")) {
            val dayName = when (weekday.groupValues[1]) {
                "Mon" -> if (language == "zu") "Msombuluko" else "Ma"
                "Tue" -> if (language == "zu") "Lwesibili" else "Di"
                "Wed" -> if (language == "zu") "Lwesithathu" else "Wo"
                "Thu" -> if (language == "zu") "Lwesine" else "Do"
                "Fri" -> if (language == "zu") "Lwesihlanu" else "Vr"
                "Sat" -> if (language == "zu") "Mgqibelo" else "Sa"
                else -> if (language == "zu") "Sonto" else "So"
            }
            return dayName + weekday.groupValues[2]
        }
        val levelProgress = Regex("^Level (\\d+) progress$").matchEntire(value)
        if (levelProgress != null) return when (language) {
            "zu" -> "Ukuqhubeka kwezinga ${levelProgress.groupValues[1]}"
            "af" -> "Vordering met vlak ${levelProgress.groupValues[1]}"
            else -> value
        }
        val pointsToLevel = Regex("^(\\d+) pts to Level (\\d+)$").matchEntire(value)
        if (pointsToLevel != null) return when (language) {
            "zu" -> "${pointsToLevel.groupValues[1]} amaphuzu ukuya ezingeni ${pointsToLevel.groupValues[2]}"
            "af" -> "${pointsToLevel.groupValues[1]} punte tot vlak ${pointsToLevel.groupValues[2]}"
            else -> value
        }
        val levelAndPoints = Regex("^Level (\\d+) · (\\d+) pts · 🔥 (\\d+)$").matchEntire(value)
        if (levelAndPoints != null) return when (language) {
            "zu" -> "Izinga ${levelAndPoints.groupValues[1]} · ${levelAndPoints.groupValues[2]} amaphuzu · 🔥 ${levelAndPoints.groupValues[3]}"
            "af" -> "Vlak ${levelAndPoints.groupValues[1]} · ${levelAndPoints.groupValues[2]} punte · 🔥 ${levelAndPoints.groupValues[3]}"
            else -> value
        }
        val hey = Regex("^Hey, (.+) 👋$").matchEntire(value)
        if (hey != null) return when (language) {
            "zu" -> "Sawubona, ${hey.groupValues[1]} 👋"
            "af" -> "Hallo, ${hey.groupValues[1]} 👋"
            else -> value
        }
        val reflectionsCount = Regex("^(\\d+) reflections$").matchEntire(value)
        if (reflectionsCount != null) return when (language) {
            "zu" -> "Ukuzindla okungu-${reflectionsCount.groupValues[1]}"
            "af" -> "${reflectionsCount.groupValues[1]} refleksies"
            else -> value
        }
        val bestStreak = Regex("^Best (\\d+)d$").matchEntire(value)
        if (bestStreak != null) return when (language) {
            "zu" -> "Engcono ${bestStreak.groupValues[1]}d"
            "af" -> "Beste ${bestStreak.groupValues[1]}d"
            else -> value
        }
        val xpProgress = Regex("^(\\d+) / (\\d+) xp$").matchEntire(value)
        if (xpProgress != null) return when (language) {
            "zu" -> "${xpProgress.groupValues[1]} / ${xpProgress.groupValues[2]} amaphuzu"
            "af" -> "${xpProgress.groupValues[1]} / ${xpProgress.groupValues[2]} xp"
            else -> value
        }
        val levelTitle = Regex("^Level (\\d+)$").matchEntire(value)
        if (levelTitle != null) return when (language) {
            "zu" -> "Izinga ${levelTitle.groupValues[1]}"
            "af" -> "Vlak ${levelTitle.groupValues[1]}"
            else -> value
        }
        val percentToLevel = Regex("^(\\d+)% to Level (\\d+)$").matchEntire(value)
        if (percentToLevel != null) return when (language) {
            "zu" -> "${percentToLevel.groupValues[1]}% ukuya ezingeni ${percentToLevel.groupValues[2]}"
            "af" -> "${percentToLevel.groupValues[1]}% tot vlak ${percentToLevel.groupValues[2]}"
            else -> value
        }
        val pointsToNextLevel = Regex("^(\\d+) pts to next level$").matchEntire(value)
        if (pointsToNextLevel != null) return when (language) {
            "zu" -> "${pointsToNextLevel.groupValues[1]} amaphuzu ukuya ezingeni elilandelayo"
            "af" -> "${pointsToNextLevel.groupValues[1]} punte tot die volgende vlak"
            else -> value
        }
        val countTimes = Regex("^(\\d+) times$").matchEntire(value)
        if (countTimes != null) return when (language) {
            "zu" -> "Izikhathi ezingu-${countTimes.groupValues[1]}"
            "af" -> "${countTimes.groupValues[1]} keer"
            else -> value
        }
        val earnedDate = Regex("^Earned (.+)$").matchEntire(value)
        if (earnedDate != null) return when (language) {
            "zu" -> "Kutholwe ${earnedDate.groupValues[1]}"
            "af" -> "Verdien ${earnedDate.groupValues[1]}"
            else -> value
        }
        return value
    }
}
