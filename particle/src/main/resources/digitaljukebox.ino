/**
 * Digial Jukebox, Scott C. Sosna, Copyright 2019
 **/

//  Pin Manamgement
const int buttonPin = D2;
const int greenLED = D1;
const int potentiometerPin = A0;
const int redLED = D0;

//  Morse code constant values
const int LENGTH_DASH_MILLI = 200;
const int LENGTH_DOT_MILLI = 65;
const int LENGTH_BETWEEN_LETTER_MILLI = 50;
const int LENGTH_BETWEEN_WORD_MILLI = 200;

// Messenging/event statics
const char *EVENT_ANY_MESSAGE   = "jukebox.message";
const char *EVENT_BASE_FILTER   = "jukebox";
const char *EVENT_SONG_CHANGED  = "jukebox.song.changed";
const char *EVENT_VOLUME_CHANGE = "jukebox.volume.change";

// Variables to keep context for button pushes
int buttonState;                                // the current reading from the input pin
int lastButtonState = LOW;                      // the previous reading from the input pin
unsigned long lastDebounceTime = 0;             // the last time the output pin was toggled
const int BUTTON_PRESS_LONG = 1500;             // milliseconds indicating a long button push
const int BUTTON_PRESS_SHORT = 300;             // nukkusecibds indicating a short button push
const unsigned long BUTTON_DEBOUNCE_DELAY = 100; // the debounce time; increase if the output flickers
const char *BUTTON_WEBHOOK_MUTE  = "jukebox.webhook.mute_unmute";
const char *BUTTON_WEBHOOK_NEXT  = "jukebox.webhook.next";
const char *BUTTON_WEBHOOK_START = "jukebox.webhook.start_stop";

//  Potentiometer variables to track when changes occur.
boolean potChangePublished = true;          //  has the most recent potentiometer change been published?
int potChangeCount = 0;                     //  how many times has the value stayed the same (or within limit)
int potCurrentValue;                        //  current value of the pot
unsigned long potEventPublished = millis(); //  time in millis that last event was published, attempt to limit
const int potDifferenceLimit = 50;          //  ignore a changed value whose difference isn't greater than this
const int potStableCount = 50;              //  how many identical readings are required before we publish a value change?

// Defines the internal LED as a red, solid for the morse code.
LEDStatus morseCodeLED (RGB_COLOR_RED, LED_PATTERN_SOLID, LED_PRIORITY_IMPORTANT);
LEDStatus morseCodeBlank (RGB_COLOR_WHITE, LED_PATTERN_SOLID, LED_PRIORITY_NORMAL);

/**
 * Morse code translation for ASCII characters between ! and Z
 **/
const char *morseTranslation[] {
    "-.-.--",   // exclamation mark
    ".-..-.",   // double quotes
    "-....-",   // hash (used hypeh)
    "-....-",   // dollar sign
    "-....-",   // percent
    ".-...",    // ampersand
    ".-..-.",   // single quote (used double quotes)
    "-.--.",    // open parentheses
    "-.--.-",   // close parentheses
    "-....-",   // asterix (used hyphen)
    ".-.-.",    // plus sign
    "--..--",   // comma
    "-....-",   // hyphen
    ".-.-.-",   // period
    "-..-.",    // slash
    "-----",    // 0
    ".----",    // 1
    "..---",    // 2
    "...--",    // 3
    "....-",    // 4
    ".....",    // 5
    "-....",    // 6
    "--...",    // 7
    "---..",    // 8
    "----.",    // 9
    "---...",   // :
    "---...",   // ; (used colon)
    "-....-",   // < (used hyphen)
    "-...-",    // =
    "-....-",   // > (used hyphen)
    "..--..",   // ?
    ".--.-.",   // @
    ".-",       // A
    "-...",     // B
    "-.-.",     // C
    "-..",      // D
    ".",        // E
    "..-.",     // F
    "--."       // G
    "....",     // H
    "..",       // I
    ".---",     // J
    "-.-",      // K
    ".-..",     // L
    "--",       // M
    "-.",       // N
    "---",      // O
    ".--.",     // P
    "--.-",     // Q
    ".-.",      // R
    "...",      // S
    "-",        // T
    "..-",      // U
    "...-",     // V
    ".--",      // W
    "-..-",     // X
    "-.--",     // Y
    "--..",     // Z
    "-.--.",    // [ (used open parenthese)
    "-..-.",    // \ (used slash)
    "-.--.-",   // ] (used close parentheses)
    "-....-",   // ^ (used hyphen)
    "-....-",   // _ (used hyphen)
    ".-.-.-",   // .
    ".-",       // a
    "-...",     // b
    "-.-.",     // c
    "-..",      // d
    ".",        // e
    "..-.",     // f
    "--."       // g
    "....",     // h
    "..",       // i
    ".---",     // j
    "-.-",      // k
    ".-..",     // l
    "--",       // m
    "-.",       // n
    "---",      // o
    ".--.",     // p
    "--.-",     // q
    ".-.",      // r
    "...",      // s
    "-",        // t
    "..-",      // u
    "...-",     // v
    ".--",      // w
    "-..-",     // x
    "-.--",     // y
    "--..",     // z
};


/**
 * Toggle green LED on or off
 **/
int setGreenLED(String command) {
    return setLED(greenLED, command);
}

/**
 * Toggle red LED on or off
 **/
int toggleRedLED(String command) {
    setLED(redLED, "on");
    delay(100);
    setLED(redLED, "off");
    return 0;
}

/**
 * Do a digital write, either making the pin high or low.
 **/
int setLED (int pin, String command) {

    if (command=="on") {
        digitalWrite(pin, HIGH);
        return 1;
    } else if (command=="off") {
        digitalWrite(pin, LOW);
        return 0;
    } else {
        return -1;
    }
}

/**
 * Translate a string into morse code, flashing the built-in LED for dots/dashes
 **/
void translateStringToMorse (const char *toTranslate) {

    //  Turn the LED white so the red of the dot/dashes stand out.
    morseCodeBlank.setActive(true);

    //  Increment through the character array;
    boolean allValid = true;
    while (*toTranslate != 0) {
        if (*toTranslate != ' ') {
            allValid &= translateCharacterToMorse (*toTranslate);
        } else {
            delay (LENGTH_BETWEEN_WORD_MILLI);
        }
        toTranslate++;
    }

    //  Notify if there was an invalid character
    if (!allValid) {
        Serial.println ("Invalid characters found");
    }

    //  On completion of the translation, turn the background off
    morseCodeBlank.setActive(false);
}


/**
 * Flash the built-in LED red for one character translated into morse code
 **/
boolean translateCharacterToMorse (char letter) {

    boolean allValidChars = true;
    if (letter >= '!' && letter <= 'z') {

        //  Get the translation into Morse Code for this letter
        const char * morseOneChar = morseTranslation[letter - '!'];

        while (*morseOneChar != 0) {
            morseCodeLED.setActive(true);
            delay (*morseOneChar == '.' ? LENGTH_DOT_MILLI : LENGTH_DASH_MILLI);
            morseCodeLED.setActive(false);
            delay (LENGTH_BETWEEN_LETTER_MILLI);
            morseOneChar++;
        }
        delay (LENGTH_BETWEEN_LETTER_MILLI);
    } else {
        allValidChars = false;
    }

    return allValidChars;
}

/**
 * An arbitrary message has been published and we'll morse-code whatever it is.
 **/
void messageHandler (const char *data) {

    //  Miscellaneous message received.
    Serial.printlnf ("Message received: %s", data);
    translateStringToMorse (data);
}

/**
 * What to do when the Particle is notified of a song change.
 **/
void songChangeHandler (const char *data) {

    //  What is the new song title/artist?
    Serial.printlnf ("Song: %s", data);
    translateStringToMorse (data);
}

/**
 * Handler processes events published as messages to cloud
 **/
void eventHandler (const char *event, const char *data) {

    //  Is the event a song change?
    if (strcmp (EVENT_SONG_CHANGED, event) == 0) {
        songChangeHandler (data);
    } else if (strcmp (EVENT_ANY_MESSAGE, event) == 0) {
        messageHandler (data);
    }
}

/**
 * When called, an event to cause a song skip is generated.
 **/
void publishVolumeChange (int potValue) {
    if (Particle.connected() && (millis() - potEventPublished) > 1000) {

        //  Calculate the volume as a floating point numbeer between 0.0 and 1.0
        float newValue = (float) potValue / 4100;

        Serial.printlnf("Volume event published: %f", newValue);
        Particle.publish (EVENT_VOLUME_CHANGE, String(newValue), PUBLIC);
        potEventPublished = millis();
    }
}

/**
 * Read the value from the pot.
 **/
int readPotentiometer () {
    return analogRead (potentiometerPin);
}

/**
 * Determining whether or not the button has been pushed.  Adapted from Arduino tutorial
 * https://www.arduino.cc/en/tutorial/debounce
 **/
int checkForButtonPress () {

    // read the state of the switch into a local variable:
    int reading = digitalRead(buttonPin);

    // check to see if you just pressed the button
    // (i.e. the input went from LOW to HIGH), and you've waited long enough
    // since the last press to ignore any noise:

    // If the switch changed, due to noise or pressing:
    unsigned long delay = millis() - lastDebounceTime;
    if (reading != lastButtonState) {
        // reset the debouncing timer
        lastDebounceTime = millis();
    }

    if (delay > BUTTON_DEBOUNCE_DELAY) {
        // whatever the reading is at, it's been there for longer than the debounce
        // delay, so take it as the actual current state:

        // if the button state has changed:
        if (reading != buttonState) {
            Serial.printlnf("Button changed: %d", reading);
            buttonState = reading;
            if (buttonState == 1) {
                determineButtonAction (delay);
            }
        }
    }

    // save the reading. Next time through the loop, it'll be the lastButtonState:
    lastButtonState = reading;
}

/**
 * Check for changes in the value for potentiometer and potentially publish the change
 * for a consumer to act upon.
 **/
void checkForChangedPotentiometer() {

    //  Read the current value
    int val = readPotentiometer();

    //  Did the value change?
    if (abs(val - potCurrentValue) > potDifferenceLimit) {
        //  To avoid overly-aggressive events for every little change in value, we'll
        //  wait until the value hasn't changed for a period of time.  When the first
        //  change comes in, update the value and set things to track a change in progress
        potCurrentValue = val;
        potChangePublished = false;
        potChangeCount = 0;
    } else {
        if (!potChangePublished && potChangeCount++ > potStableCount) {
            //  The changed value hasn't been published BUT we've exceeded the number
            //  of checks and we can now publish
            publishVolumeChange (val);
            potChangePublished = true;
        } else {
            //  Nothing to do, we've published the most recent change and we're not managing
            //  an active change, so just go on.
        }
    }
}

/**
 * Based on the length of the button push, take an action
 **/
void determineButtonAction (const unsigned long delay) {

    Serial.printlnf("%d", delay);
    if (delay < BUTTON_PRESS_SHORT) {
        buttonActionShort();
    } else if (delay > BUTTON_PRESS_LONG) {
        buttonActionLong();
    } else {
        buttonActionNormal();
    }
}

/**
 * Long button push triggers a mute/unmute webhook call
 **/
void buttonActionLong () {
    //  Action to take when a long button press occurs.
        Serial.println ("Action MUTE UNMUTE");
        Particle.publish(BUTTON_WEBHOOK_MUTE, NULL, PRIVATE);
}

/**
 * Normal button push triggers a start/stop webhook call
 **/
void buttonActionNormal () {
    //  Action to take when a normal button push occurs.
        Serial.println ("Action START STOP");
        Particle.publish(BUTTON_WEBHOOK_START, NULL, PRIVATE);
}

/**
 * Short button push triggers a next webhook call
 **/
void buttonActionShort () {
    //  Action to take with a short button push occurs.
        Serial.println ("Action NEXT");
        Particle.publish(BUTTON_WEBHOOK_NEXT, NULL, PRIVATE);
}

/**
 * Standard Arduino setup method, called once
 **/
void setup() {

    //  Set the mode correctly for each pin used.
    pinMode(greenLED, OUTPUT);
    pinMode(redLED, OUTPUT);
    pinMode(buttonPin, INPUT);
    pinMode(potentiometerPin, INPUT);

    //  register the functions
    Particle.function("green", setGreenLED);
    Particle.function("red", toggleRedLED);
    Serial.begin(9600);

    //  Register the event handlers
    Particle.subscribe (EVENT_BASE_FILTER, eventHandler, ALL_DEVICES);

    //  Initialize values for the potentiometer.
    potCurrentValue = readPotentiometer();
}

void loop() {

    //  Check the potentiometer for a changed value.
    checkForChangedPotentiometer();

    //  Has the button been pushed?
    checkForButtonPress();
}

