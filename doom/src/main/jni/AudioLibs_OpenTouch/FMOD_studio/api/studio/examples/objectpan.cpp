/*==============================================================================
Object Panning Example
Copyright (c), Firelight Technologies Pty, Ltd 2015-2020.

This example demonstrates the FMOD object panner. The usage is completely
transparent to the API, the only difference is how the event is authored in the
FMOD Studio tool.

To hear the difference between object panning and normal panning this example
has two events (one configured with the normal panner, and one with the object
panner). As they move around the listener you may toggle between panning method
and two different sounds.

Object panning requires compatible hardware such as a Dolby Atmos amplifier or
a Playstation VR headset. For cases when the necessary hardware is not available
FMOD will fallback to standard 3D panning.

NOTE! Currently the Atmos output mode requires a dll to be supplied via support.
Atmos access needs to be granted via Dolby first.  Write to support@fmod.com 
for more.

==============================================================================*/
#include "fmod_studio.hpp"
#include "fmod.hpp"
#include "common.h"
#include <math.h>

int FMOD_Main()
{
    bool isOnGround = false;
    
    void *extraDriverData = NULL;
    Common_Init(&extraDriverData);

    FMOD::Studio::System *system = NULL;
    ERRCHECK( FMOD::Studio::System::create(&system) );

    // The example Studio project is authored for 5.1 sound, so set up the system output mode to match
    FMOD::System* lowLevelSystem = NULL;
    ERRCHECK( system->getLowLevelSystem(&lowLevelSystem) );
    ERRCHECK( lowLevelSystem->setSoftwareFormat(0, FMOD_SPEAKERMODE_5POINT1, 0) );

    // Attempt to initialize with a compatible object panning output
    FMOD_RESULT result = lowLevelSystem->setOutput(FMOD_OUTPUTTYPE_AUDIO3D);
    if (result != FMOD_OK)
    {
        result = lowLevelSystem->setOutput(FMOD_OUTPUTTYPE_WINSONIC);
        if (result == FMOD_OK)
        {
            ERRCHECK( lowLevelSystem->setSoftwareFormat(0, FMOD_SPEAKERMODE_7POINT1POINT4, 0) );
        }
    }

    int numDrivers = 0;
    ERRCHECK( lowLevelSystem->getNumDrivers(&numDrivers) );

    if (numDrivers == 0)
    {
        ERRCHECK( lowLevelSystem->setDSPBufferSize(512, 4) );
        ERRCHECK( lowLevelSystem->setOutput(FMOD_OUTPUTTYPE_AUTODETECT) );
    }

    // Due to a bug in WinSonic on Windows, FMOD initialization may fail on some machines.
    // If you get the error "FMOD error 51 - Error initializing output device", try using
    // a different output type such as FMOD_OUTPUTTYPE_AUTODETECT
    ERRCHECK( system->initialize(1024, FMOD_STUDIO_INIT_NORMAL, FMOD_INIT_NORMAL, extraDriverData) );
    
    // Load everything needed for playback
    FMOD::Studio::Bank *masterBank = NULL;
    FMOD::Studio::Bank *musicBank = NULL;
    FMOD::Studio::Bank *stringsBank = NULL;
    FMOD::Studio::EventDescription *spatializerDescription = NULL;
    FMOD::Studio::EventInstance *spatializerInstance = NULL;
    float spatializer;
    float radioFrequency;

    ERRCHECK( system->loadBankFile(Common_MediaPath("Master Bank.bank"), FMOD_STUDIO_LOAD_BANK_NORMAL, &masterBank) );
    ERRCHECK( system->loadBankFile(Common_MediaPath("Music.bank"), FMOD_STUDIO_LOAD_BANK_NORMAL, &musicBank) );
    ERRCHECK( system->loadBankFile(Common_MediaPath("Master Bank.strings.bank"), FMOD_STUDIO_LOAD_BANK_NORMAL, &stringsBank) );
    ERRCHECK( system->getEvent("event:/Music/Radio Station", &spatializerDescription) );
    ERRCHECK( spatializerDescription->createInstance(&spatializerInstance) );
    ERRCHECK( spatializerInstance->start() );
    ERRCHECK( spatializerInstance->getParameterValue("Freq", NULL, &radioFrequency) );
    ERRCHECK( spatializerInstance->getParameterValue("Spatializer", NULL, &spatializer) );

    do
    {
        Common_Update();

        ERRCHECK(spatializerInstance->getParameterValue("Freq", NULL, &radioFrequency));
        ERRCHECK(spatializerInstance->getParameterValue("Spatializer", NULL, &spatializer));

        if (Common_BtnPress(BTN_ACTION1))
        {
            if (radioFrequency == 3.00f)
            {
                ERRCHECK(spatializerInstance->setParameterValue("Freq", 0.00f));
            }
            else
            {
                ERRCHECK(spatializerInstance->setParameterValue("Freq", (radioFrequency + 1.50f)));
            }
        }

        if (Common_BtnPress(BTN_ACTION2))
        {
            if (spatializer == 1.00)
            {
                ERRCHECK(spatializerInstance->setParameterValue("Spatializer", 0.00f));
            }
            else
            {
                ERRCHECK(spatializerInstance->setParameterValue("Spatializer", 1.00f));
            }
        }

        if (Common_BtnPress(BTN_ACTION3))
        {
            isOnGround = !isOnGround;
        }

        FMOD_3D_ATTRIBUTES vec = { };
        vec.forward.z = 1.0f;
        vec.up.y = 1.0f;
        static float t = 0;
        vec.position.x = sinf(t) * 3.0f;        /* Rotate sound in a circle */
        vec.position.z = cosf(t) * 3.0f;        /* Rotate sound in a circle */
        t += 0.03f;

        if (isOnGround)
        {
            vec.position.y = 0;                     /* At ground level */
        }
        else
        {
            vec.position.y = 5.0f;                  /* Up high */
        }

        ERRCHECK( spatializerInstance->set3DAttributes(&vec) );

        FMOD_3D_ATTRIBUTES listener_vec = { };
        listener_vec.forward.z = 1.0f;
        listener_vec.up.y = 1.0f;

        ERRCHECK( system->setListenerAttributes(0, &listener_vec) );
        ERRCHECK( system->update() );

        Common_Draw("==================================================");
        Common_Draw("Object Panning Example.");
        Common_Draw("Copyright (c) Firelight Technologies 2015-2020.");
        Common_Draw("==================================================");
        Common_Draw("");
        Common_Draw("Current radio station: %f - %s", radioFrequency, (radioFrequency == 0.00f) ? "Rock" : (radioFrequency == 1.50f) ? "Lo-fi" : "Hip hop");
        Common_Draw("Radio is %s.", isOnGround ? "on the ground" : "up in the air");
        Common_Draw("");
        Common_Draw("Press %s to switch stations.", Common_BtnStr(BTN_ACTION1));
        Common_Draw("Press %s to switch panning to %s.", Common_BtnStr(BTN_ACTION2), (spatializer == 0.00f) ? "Standard 3D Panner" : "Object Panner");
        Common_Draw("Press %s to elevate the event instance.", Common_BtnStr(BTN_ACTION3));
        Common_Draw("");
        Common_Draw("Press %s to quit", Common_BtnStr(BTN_QUIT));

        Common_Sleep(50);
    } while (!Common_BtnPress(BTN_QUIT));
    
    ERRCHECK( stringsBank->unload() );
    ERRCHECK( musicBank->unload() );
    ERRCHECK( system->release() );

    Common_Close();

    return 0;
}