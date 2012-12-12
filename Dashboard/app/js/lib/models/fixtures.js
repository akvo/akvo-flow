FLOW.SurveyGroup.FIXTURES = [{
    id: 1,
    keyId: 1,
    code: 'Urban sanitation surveys'
}, {
    id: 2,
    keyId: 2,
    code: 'Elise Surveys'
}, {
    id: 3,
    keyId: 3,
    code: 'Test Survey group'
}, {
    id: 4,
    keyId: 4,
    code: 'Upande - SNVVERMIS'
}, {
    id: 5,
    keyId: 5,
    code: 'Akvo test surveys'
}];


FLOW.Survey.FIXTURES = [{
    id: 1,
    keyId: 1,
    displayName: 'Water point survey',
    name: 'Water point survey',
    surveyGroupId: 1
}, {
    id: 2,
    keyId: 2,
    displayName: 'Sanitation survey',
    name: 'Sanitation survey',
    surveyGroupId: 1
}, {
    id: 3,
    keyId: 3,
    displayName: 'Baseline WASH',
    name: 'Baseline WASH',
    surveyGroupId: 1
}, {
    id: 4,
    keyId: 4,
    displayName: 'Akvo RSR update',
    name: 'Akvo RSR update',
    surveyGroupId: 1
}, {
    id: 5,
    keyId: 5,
    displayName: 'Akvo update',
    name: 'Akvo update',
    surveyGroupId: 1
}, {
    id: 6,
    keyId: 6,
    displayName: 'Loics survey',
    name: 'Loics survey',
    surveyGroupId: 1
}, {
    id: 7,
    keyId: 7,
    displayName: 'Farmer survey',
    name: 'Farmer survey',
    surveyGroupId: 1
}, {
    id: 8,
    keyId: 8,
    displayName: 'Rabbit',
    name: 'Rabbit',
    surveyGroupId: 1
}, {
    id: 9,
    keyId: 9,
    displayName: 'Rabbit II',
    name: 'Rabbit II',
    surveyGroupId: 1
}];


FLOW.QuestionGroup.FIXTURES = [{
    id: 1,
    keyId: 1,
    surveyId: 1,
    order: 1,
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu. ",
    code: 'Location',
    displayName: 'Location'

}, {
    id: 2,
    keyId: 2,
    surveyId: 1,
    order: 2,
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
    code: 'Occupation',
    displayName: 'Occupation'

}, {
    id: 3,
    keyId: 3,
    surveyId: 1,
    order: 3,
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
    code: 'Water system',
    displayName: 'Water system'

}, {
    id: 4,
    keyId: 4,
    surveyId: 1,
    order: 4,
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
    code: 'Sanitation system',
    displayName: 'Sanitation system'

}, {
    id: 5,
    keyId: 5,
    surveyId: 2,
    order: 5,
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
    code: 'Something else',
    displayName: 'Something else'

}

];

FLOW.Question.FIXTURES = [{
    id: 1,
    keyId: 1,
    text: 'What is the name of the community?',
    displayName: 'What is the name of the community?',
    mandatory: false,
    order: 1,
    questionTypeString: 'freeText',
    questionSetId: 1
}, {
    id: 2,
    keyId: 2,
    text: 'What is your occupation?',
    displayName: 'What is your occupation?',
    mandatory: false,
    order: 2,
    questionTypeString: 'option',
    questionSetId: 1
}, {
    id: 3,
    keyId: 3,
    text: 'How much children do you have?',
    displayName: 'How much children do you have?',
    mandatory: false,
    order: 3,
    questionTypeString: 'number',
    questionSetId: 1
}, {
    id: 4,
    keyId: 4,
    text: 'Please take a geolocation',
    displayName: 'Please take a geolocation',
    mandatory: false,
    order: 4,
    questionTypeString: 'geoLoc',
    questionSetId: 1
}, {
    id: 5,
    keyId: 5,
    text: 'Please take a picture',
    displayName: 'Please take a picture',
    mandatory: false,
    order: 5,
    questionTypeString: 'photo',
    questionSetId: 1
}, {
    id: 6,
    keyId: 6,
    text: 'Please make a video',
    displayName: 'Please make a video',
    questionTypeString: 'video',
    order: 6,
    mandatory: false,
    questionSetId: 1
}, {
    id: 7,
    keyId: 7,
    text: 'What is the date today?',
    displayName: 'What is the date today?',
    questionTypeString: 'date',
    order: 7,
    mandatory: false,
    questionSetId: 1
}];

FLOW.QuestionOption.FIXTURES = [{
    id: 1,
    keyId: 1,
    text: 'teacher',
    questionId: 1
}, {
    id: 2,
    keyId: 2,
    text: 'cook',
    questionId: 1
}, {
    id: 3,
    keyId: 3,
    text: 'minister',
    questionId: 1
}, {
    id: 4,
    keyId: 4,
    text: 'programmer',
    questionId: 1
}];


FLOW.DeviceGroup.FIXTURES = [{
    id: 1,
    displayName: 'Malawi',
    code: 'malawi'
}, {
    id: 2,
    displayName: 'Bolivia',
    code: 'bolivia'
}];

FLOW.Device.FIXTURES = [{
    id: 1,
    keyId: 1,
    phoneNumber: "3f:d4:8f:2a:8c:9f",
    deviceIdentifier: "Keri phone 1",
    deviceGroup: "WFP general",
    lastUpdate: "21 May 2012 20:30:00",
    lastLocationBeaconTime: "22 May 2012 20:30:00",
    lastKnownLat: 23.132132321,
    lastKnownLong: 12.23232332
}, {
    id: 2,
    keyId: 2,
    phoneNumber: "2a:8c:9f:3f:d4:8f",
    deviceIdentifier: " Keri phone 2",
    deviceGroup: "WFP general",
    lastUpdate: "21 Apr 2012 20:30:00",
    lastLocationBeaconTime: "27 Feb 2012 20:30:00",
    lastKnownLat: 43.33434343,
    lastKnownLong: -5.32332343
}, {
    id: 3,
    keyId: 3,
    phoneNumber: "31648492710",
    deviceIdentifier: "Marks phone",
    deviceGroup: "WFP general",
    lastUpdate: "01 Sep 2012 20:30:00",
    lastLocationBeaconTime: "12 Aug 2012 20:30:00",
    lastKnownLat: 34.222334234,
    lastKnownLong: -7.44343434
}, {
    id: 4,
    keyId: 4,
    phoneNumber: "34029392833",
    deviceIdentifier: "WFP colombia-1",
    deviceGroup: "Colombia",
    lastUpdate: "21 Aug 2012 20:30:00",
    lastLocationBeaconTime: "04 Jan 2012 20:30:00",
    lastKnownLat: 2.334343434,
    lastKnownLong: -23.33433432
}, {
    id: 5,
    keyId: 5,
    phoneNumber: "3f:d4:8f:8b:8c:3e",
    deviceIdentifier: "WFP colombia 2",
    deviceGroup: "Colombia",
    lastUpdate: "12 Apr 2012 20:30:00",
    lastLocationBeaconTime: "31 Oct 2012 20:30:00",
    lastKnownLat: 8.55454435,
    lastKnownLong: 54.88399473
}, {
    id: 6,
    keyId: 6,
    phoneNumber: "2a:8c:9f:3f:d4:8f",
    deviceIdentifier: "WFP phone 3",
    deviceGroup: "Malawi",
    lastUpdate: "17 Jul 2012 20:30:00",
    lastLocationBeaconTime: "16 Jun 2012 20:30:00",
    lastKnownLat: 23.988332,
    lastKnownLong: -64.88399483
}, {
    id: 7,
    keyId: 7,
    phoneNumber: "3403928293",
    deviceIdentifier: "WFP phone 4",
    deviceGroup: "Malawi",
    lastUpdate: "11 Dec 2012 20:30:00",
    lastLocationBeaconTime: "14 Nov 2012 20:30:00",
    lastKnownLat: 23.3323432,
    lastKnownLong: 9.88873633
}];

FLOW.SurveyedLocale.FIXTURES = [{
    description: "Welkom in Amsterdam!",
    keyId: 1,
    latitude: 52.370216,
    longitude: 4.895168,
    typeMark: "WATER_POINT"
}, {
    description: "Welcome to London!",
    keyId: 2,
    latitude: 51.507335,
    longitude: -0.127683,
    typeMark: "WATER_POINT"
}, {
    description: "Välkommen till Stockholm!",
    keyId: 3,
    latitude: 59.32893,
    longitude: 18.06491,
    typeMark: "WATER_POINT"
}];

FLOW.Placemark.FIXTURES = [{
    longitude: 36.76034601,
    latitude: -1.29624521,
    collectionDate: 1328620272000,
    markType: "WATER_POINT",
    id: 530003
}, {
    longitude: 36.76052649,
    latitude: -1.29624207,
    collectionDate: 1331040590000,
    markType: "WATERPOINT",
    id: 545030
}, {
    longitude: 36.7545783327,
    latitude: -1.35175386504,
    collectionDate: 1331005669000,
    markType: "WATER_POINT",
    id: 549003
}, {
    longitude: 36.74724467,
    latitude: -1.26103461,
    collectionDate: 1333221136000,
    markType: "WATERPOINT",
    id: 606070
}, {
    longitude: 36.69691894,
    latitude: -1.25285542,
    collectionDate: 1333221922000,
    markType: "WATERPOINT",
    id: 609077
}, {
    longitude: 35.07498217,
    latitude: -0.15946829,
    collectionDate: 1334905070000,
    markType: "WATERPOINT",
    id: 732033
}, {
    longitude: 36.76023113,
    latitude: -1.29614013,
    collectionDate: 1335258461000,
    markType: "WATER_POINT",
    id: 761148
}, {
    longitude: 36.7905733168,
    latitude: -1.85040885561,
    collectionDate: 1339065449000,
    markType: "WATER_POINT",
    id: 950969
}, {
    longitude: 35.19765058,
    latitude: -0.15885514,
    collectionDate: 1339660634000,
    markType: "WATER_POINT",
    id: 990840
}, {
    longitude: 35.23715568,
    latitude: -0.16715051,
    collectionDate: 1340173295000,
    markType: "WATERPOINT",
    id: 1029003
}];

FLOW.PlacemarkDetail.FIXTURES = [{
    stringValue: "Community (CBO)",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "1. WP Ownership",
    placemarkId: 732033,
    id: 734238
}, {
    stringValue: "Functional ( in use)",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "1.Functional status",
    placemarkId: 732033,
    id: 734234
}, {
    stringValue: "Unsafe",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "2a. Quantitative in-field assessment",
    placemarkId: 732033,
    id: 735246
}, {
    stringValue: "Coloured (whitish- brownish)",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "2b.Qualitative in-field assessment",
    placemarkId: 732033,
    id: 735245
}, {
    stringValue: "Good- practically always",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "3. Reliability",
    placemarkId: 732033,
    id: 734235
}, {
    stringValue: "Yes",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "B.Sufficient for HHs",
    placemarkId: 732033,
    id: 732228
}, {
    stringValue: "Yes",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "C. Sufficient for livestock",
    placemarkId: 732033,
    id: 735242
}, {
    stringValue: "ahero youth",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "CBO, specify",
    placemarkId: 732033,
    id: 732222
}, {
    stringValue: "Unknown",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Committee in place:",
    placemarkId: 732033,
    id: 735249
}, {
    stringValue: "all",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Community, specify",
    placemarkId: 732033,
    id: 732224
}, {
    stringValue: "Name one",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Contact one",
    placemarkId: 732033,
    id: 735244
}, {
    stringValue: "40",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "D. HHs # served/day",
    placemarkId: 732033,
    id: 735241
}, {
    stringValue: "20/04/12",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Date Record",
    placemarkId: 732033,
    id: 728181
}, {
    stringValue: "No",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Do you have an SPA?",
    placemarkId: 732033,
    id: 734236
}, {
    stringValue: "community",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Funded by",
    placemarkId: 732033,
    id: 728179
}, {
    stringValue: "-0.16252854|35.07743752|1136.800048828125|7gs8a46",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "GPS reading",
    placemarkId: 732033,
    id: 732229
}, {
    stringValue: "alex",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Interviewee",
    placemarkId: 732033,
    id: 728178
}, {
    stringValue: "amara",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Interviewer",
    placemarkId: 732033,
    id: 728180
}, {
    stringValue: "ahero pan",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Name of source/water point",
    placemarkId: 732033,
    id: 728182
}, {
    stringValue: "onuonga",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Names one,specify",
    placemarkId: 732033,
    id: 735247
}, {
    stringValue: "No",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "O&M cost recovery",
    placemarkId: 732033,
    id: 732225
}, {
    stringValue: "LVNWSB",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Organisation",
    placemarkId: 732033,
    id: 728183
}, {
    stringValue: "No",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Part of the piped scheme",
    placemarkId: 732033,
    id: 734229
}, {
    stringValue: "/mnt/sdcard/fieldsurvey/surveyal/8/7/9/8/5/wfpPhoto18652367987985.jpg",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Photo",
    placemarkId: 732033,
    id: 732230
}, {
    stringValue: "No",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Specify none",
    placemarkId: 732033,
    id: 732223
}, {
    stringValue: "tura",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Sub-location",
    placemarkId: 732033,
    id: 734231
}, {
    stringValue: "< 1 hour",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Time",
    placemarkId: 732033,
    id: 732227
}, {
    stringValue: "Dam/Pan(runoff harvesting)",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Unimproved",
    placemarkId: 732033,
    id: 734233
}, {
    stringValue: "ksm/040",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "WP ID",
    placemarkId: 732033,
    id: 728177
}, {
    stringValue: "Community (technician) Name/NO",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "WP Maintenance",
    placemarkId: 732033,
    id: 735248
}, {
    stringValue: "Directly managed by the CBO",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "WP Management",
    placemarkId: 732033,
    id: 734237
}, {
    stringValue: "Year round",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Water Availability",
    placemarkId: 732033,
    id: 732226
}, {
    stringValue: "None",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Water Payment",
    placemarkId: 732033,
    id: 735250
}, {
    stringValue: "30",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Water consumption per ( in dry season)",
    placemarkId: 732033,
    id: 735243
}, {
    stringValue: "Unimproved",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Water source type",
    placemarkId: 732033,
    id: 734232
}, {
    stringValue: "No",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Within WSP",
    placemarkId: 732033,
    id: 734230
}, {
    stringValue: "2004",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Year Constructed",
    placemarkId: 732033,
    id: 732231
}];


FLOW.QuestionAnswer.FIXTURES = [{
    value: "Community (CBO)",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "1. WP Ownership",
    placemarkId: 732033,
    id: 734238
}, {
    value: "Functional ( in use)",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "1.Functional status",
    placemarkId: 732033,
    id: 734234
}, {
    value: "Unsafe",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "2a. Quantitative in-field assessment",
    placemarkId: 732033,
    id: 735246
}, {
    value: "Coloured (whitish- brownish)",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "2b.Qualitative in-field assessment",
    placemarkId: 732033,
    id: 735245
}, {
    value: "Good- practically always",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "3. Reliability",
    placemarkId: 732033,
    id: 734235
}, {
    value: "Yes",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "B.Sufficient for HHs",
    placemarkId: 732033,
    id: 732228
}, {
    value: "Yes",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "C. Sufficient for livestock",
    placemarkId: 732033,
    id: 735242
}, {
    value: "ahero youth",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "CBO, specify",
    placemarkId: 732033,
    id: 732222
}, {
    value: "Unknown",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Committee in place:",
    placemarkId: 732033,
    id: 735249
}, {
    value: "all",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Community, specify",
    placemarkId: 732033,
    id: 732224
}, {
    value: "Name one",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Contact one",
    placemarkId: 732033,
    id: 735244
}, {
    value: "40",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "D. HHs # served/day",
    placemarkId: 732033,
    id: 735241
}, {
    value: "20/04/12",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Date Record",
    placemarkId: 732033,
    id: 728181
}, {
    value: "No",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Do you have an SPA?",
    placemarkId: 732033,
    id: 734236
}, {
    value: "community",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Funded by",
    placemarkId: 732033,
    id: 728179
}, {
    value: "-0.16252854|35.07743752|1136.800048828125|7gs8a46",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "GPS reading",
    placemarkId: 732033,
    id: 732229
}, {
    value: "alex",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Interviewee",
    placemarkId: 732033,
    id: 728178
}, {
    value: "amara",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Interviewer",
    placemarkId: 732033,
    id: 728180
}, {
    value: "ahero pan",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Name of source/water point",
    placemarkId: 732033,
    id: 728182
}, {
    value: "onuonga",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Names one,specify",
    placemarkId: 732033,
    id: 735247
}, {
    value: "No",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "O&M cost recovery",
    placemarkId: 732033,
    id: 732225
}, {
    value: "LVNWSB",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Organisation",
    placemarkId: 732033,
    id: 728183
}, {
    value: "No",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Part of the piped scheme",
    placemarkId: 732033,
    id: 734229
}, {
    value: "/mnt/sdcard/fieldsurvey/surveyal/8/7/9/8/5/wfpPhoto18652367987985.jpg",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Photo",
    placemarkId: 732033,
    id: 732230
}, {
    value: "No",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Specify none",
    placemarkId: 732033,
    id: 732223
}, {
    value: "tura",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Sub-location",
    placemarkId: 732033,
    id: 734231
}, {
    value: "< 1 hour",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Time",
    placemarkId: 732033,
    id: 732227
}, {
    value: "Dam/Pan(runoff harvesting)",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Unimproved",
    placemarkId: 732033,
    id: 734233
}, {
    value: "ksm/040",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "WP ID",
    placemarkId: 732033,
    id: 728177
}, {
    value: "Community (technician) Name/NO",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "WP Maintenance",
    placemarkId: 732033,
    id: 735248
}, {
    value: "Directly managed by the CBO",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "WP Management",
    placemarkId: 732033,
    id: 734237
}, {
    value: "Year round",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Water Availability",
    placemarkId: 732033,
    id: 732226
}, {
    value: "None",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Water Payment",
    placemarkId: 732033,
    id: 735250
}, {
    value: "30",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Water consumption per ( in dry season)",
    placemarkId: 732033,
    id: 735243
}, {
    value: "Unimproved",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Water source type",
    placemarkId: 732033,
    id: 734232
}, {
    value: "No",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Within WSP",
    placemarkId: 732033,
    id: 734230
}, {
    value: "2004",
    collectionDate: 1334938302000,
    metricName: "Mars / Initial question du planteur",
    questionText: "Year Constructed",
    placemarkId: 732033,
    id: 732231
}];

FLOW.SurveyInstance.FIXTURES = [{
    submitterName: "Community (CBO)",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "1. WP Ownership",
    placemarkId: 732033,
    id: 734238
}, {
    submitterName: "Functional ( in use)",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "1.Functional status",
    placemarkId: 732033,
    id: 734234
}, {
    submitterName: "Unsafe",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "2a. Quantitative in-field assessment",
    placemarkId: 732033,
    id: 735246
}, {
    submitterName: "Coloured (whitish- brownish)",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "2b.Qualitative in-field assessment",
    placemarkId: 732033,
    id: 735245
}, {
    submitterName: "Good- practically always",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "3. Reliability",
    placemarkId: 732033,
    id: 734235
}, {
    submitterName: "Yes",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "B.Sufficient for HHs",
    placemarkId: 732033,
    id: 732228
}, {
    submitterName: "Yes",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "C. Sufficient for livestock",
    placemarkId: 732033,
    id: 735242
}, {
    submitterName: "ahero youth",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "CBO, specify",
    placemarkId: 732033,
    id: 732222
}, {
    submitterName: "Unknown",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Committee in place:",
    placemarkId: 732033,
    id: 735249
}, {
    submitterName: "all",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Community, specify",
    placemarkId: 732033,
    id: 732224
}, {
    submitterName: "Name one",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Contact one",
    placemarkId: 732033,
    id: 735244
}, {
    submitterName: "40",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "D. HHs # served/day",
    placemarkId: 732033,
    id: 735241
}, {
    submitterName: "20/04/12",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Date Record",
    placemarkId: 732033,
    id: 728181
}, {
    submitterName: "No",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Do you have an SPA?",
    placemarkId: 732033,
    id: 734236
}, {
    submitterName: "community",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Funded by",
    placemarkId: 732033,
    id: 728179
}, {
    submitterName: "-0.16252854|35.07743752|1136.800048828125|7gs8a46",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "GPS reading",
    placemarkId: 732033,
    id: 732229
}, {
    submitterName: "alex",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Interviewee",
    placemarkId: 732033,
    id: 728178
}, {
    submitterName: "amara",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Interviewer",
    placemarkId: 732033,
    id: 728180
}, {
    submitterName: "ahero pan",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Name of source/water point",
    placemarkId: 732033,
    id: 728182
}, {
    submitterName: "onuonga",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Names one,specify",
    placemarkId: 732033,
    id: 735247
}, {
    submitterName: "No",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "O&M cost recovery",
    placemarkId: 732033,
    id: 732225
}, {
    submitterName: "LVNWSB",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Organisation",
    placemarkId: 732033,
    id: 728183
}, {
    submitterName: "No",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Part of the piped scheme",
    placemarkId: 732033,
    id: 734229
}, {
    submitterName: "/mnt/sdcard/fieldsurvey/surveyal/8/7/9/8/5/wfpPhoto18652367987985.jpg",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Photo",
    placemarkId: 732033,
    id: 732230
}, {
    submitterName: "No",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Specify none",
    placemarkId: 732033,
    id: 732223
}, {
    submitterName: "tura",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Sub-location",
    placemarkId: 732033,
    id: 734231
}, {
    submitterName: "< 1 hour",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Time",
    placemarkId: 732033,
    id: 732227
}, {
    submitterName: "Dam/Pan(runoff harvesting)",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Unimproved",
    placemarkId: 732033,
    id: 734233
}, {
    submitterName: "ksm/040",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "WP ID",
    placemarkId: 732033,
    id: 728177
}, {
    submitterName: "Community (technician) Name/NO",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "WP Maintenance",
    placemarkId: 732033,
    id: 735248
}, {
    submitterName: "Directly managed by the CBO",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "WP Management",
    placemarkId: 732033,
    id: 734237
}, {
    submitterName: "Year round",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Water Availability",
    placemarkId: 732033,
    id: 732226
}, {
    submitterName: "None",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Water Payment",
    placemarkId: 732033,
    id: 735250
}, {
    submitterName: "30",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Water consumption per ( in dry season)",
    placemarkId: 732033,
    id: 735243
}, {
    submitterName: "Unimproved",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Water source type",
    placemarkId: 732033,
    id: 734232
}, {
    submitterName: "No",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Within WSP",
    placemarkId: 732033,
    id: 734230
}, {
    submitterName: "2004",
    collectionDate: 1334938302000,
    surveyCode: "Mars / Initial question du planteur",
    deviceIdentifier: "Year Constructed",
    placemarkId: 732033,
    id: 732231
}];


