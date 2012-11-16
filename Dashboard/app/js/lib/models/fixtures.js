FLOW.SurveyGroup.FIXTURES = [{
	id:1,
	keyId: 1,
	code: 'Urban sanitation surveys'
}, {
	id:2,
	keyId: 2,
	code: 'Elise Surveys'
}, {
	id:3,
	keyId: 3,
	code: 'Test Survey group'
},{
	id:4,
	keyId: 4,
	code: 'Upande - SNVVERMIS'
},{
	id:5,
	keyId: 5,
	code: 'Akvo test surveys'
}];


FLOW.Survey.FIXTURES = [{
	id:1,
	keyId: 1,
	displayName: 'Water point survey',
	name:'Water point survey',
	surveyGroupId: 1
}, {
	id:2,
	keyId: 2,
	displayName: 'Sanitation survey',
	name:'Sanitation survey',
	surveyGroupId: 1
}, {
	id:3,
	keyId: 3,
	displayName: 'Baseline WASH',
	name:'Baseline WASH',
	surveyGroupId: 1
}, {
	id:4,
	keyId: 4,
	displayName: 'Akvo RSR update',
	name:'Akvo RSR update',
	surveyGroupId: 1
},
{
	id:5,
	keyId: 5,
	displayName: 'Akvo update',
	name:'Akvo update',
	surveyGroupId: 1
},
{
	id:6,
	keyId: 6,
	displayName: 'Loics survey',
	name:'Loics survey',
	surveyGroupId: 1
},
{
	id:7,
	keyId: 7,
	displayName: 'Farmer survey',
	name:'Farmer survey',
	surveyGroupId: 1
},
{
	id:8,
	keyId: 8,
	displayName: 'Rabbit',
	name:'Rabbit',
	surveyGroupId: 1
},
{
	id:9,
	keyId: 9,
	displayName: 'Rabbit II',
	name:'Rabbit II',
	surveyGroupId: 1
}];


FLOW.QuestionGroup.FIXTURES = [{
	id:1,
	keyId: 1,
	surveyId:1,
	order:1,
	description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu. ",
	name: 'Location',
	displayName: 'Location'

}, {
	id:2,
	keyId: 2,
	surveyId:1,
	order:2,
	description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
	name: 'Occupation',
	displayName: 'Occupation'

}, {
	id:3,
	keyId: 3,
	surveyId:1,
	order:3,
	description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
	name: 'Water system',
	displayName: 'Water system'

}, {
	id:4,
	keyId: 4,
	surveyId:1,
	order:4,
	description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
	name: 'Sanitation system',
	displayName: 'Sanitation system'

},
{
	id:5,
	keyId: 5,
	surveyId:2,
	order:5,
	description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
	name: 'Something else',
	displayName: 'Something else'

}

];

FLOW.Question.FIXTURES = [{
	id:1,
	keyId: 1,
	text: 'What is the name of the community?',
	displayName: 'What is the name of the community?',
	mandatory: false,
	order:1,
	questionTypeString:'freeText',
	questionSetId: 1
}, {
	id:2,
	keyId: 2,
	text: 'What is your occupation?',
	displayName: 'What is your occupation?',
	mandatory: false,
	order:2,
	questionTypeString:'option',
	questionSetId: 1
}, {
	id:3,
	keyId: 3,
	text: 'How much children do you have?',
	displayName: 'How much children do you have?',
	mandatory: false,
	order:3,
	questionTypeString:'number',
	questionSetId: 1
}, {
	id:4,
	keyId: 4,
	text: 'Please take a geolocation',
	displayName: 'Please take a geolocation',
	mandatory: false,
	order:4,
	questionTypeString:'geoLoc',
	questionSetId: 1
},{
	id:5,
	keyId: 5,
	text: 'Please take a picture',
	displayName: 'Please take a picture',
	mandatory: false,
	order:5,
	questionTypeString:'photo',
	questionSetId: 1
},	{
	id:6,
	keyId: 6,
	text: 'Please make a video',
	displayName: 'Please make a video',
	questionTypeString:'video',
	order:6,
	mandatory: false,
	questionSetId: 1
},	{
	id:7,
	keyId: 7,
	text: 'What is the date today?',
	displayName: 'What is the date today?',
	questionTypeString:'date',
	order:7,
	mandatory: false,
	questionSetId: 1
}];

FLOW.QuestionOption.FIXTURES = [{
	id:1,
	keyId: 1,
	text: 'teacher',
	questionId: 1
}, {
	id:2,
	keyId: 2,
	text: 'cook',
	questionId: 1
},{
	id:3,
	keyId: 3,
	text: 'minister',
	questionId: 1
},{
	id:4,
	keyId: 4,
	text: 'programmer',
	questionId: 1
}];


FLOW.DeviceGroup.FIXTURES = [
{
	id:1,
	displayName:'Malawi',
	code:'malawi'
},{
	id:2,
	displayName:'Bolivia',
	code:'bolivia'
}
];

FLOW.Device.FIXTURES = [{
	id:1,
	keyId:1,
	phoneNumber:"3f:d4:8f:2a:8c:9f",
	deviceIdentifier:"Keri phone 1",
	deviceGroup:"WFP general",
	lastUpdate:"21 May 2012 20:30:00",
	lastLocationBeaconTime:"22 May 2012 20:30:00",
	lastKnownLat:23.132132321,
	lastKnownLong:12.23232332,
}, {
	id:2,
	keyId:2,
	phoneNumber:"2a:8c:9f:3f:d4:8f",
	deviceIdentifier:" Keri phone 2",
	deviceGroup:"WFP general",
	lastUpdate:"21 Apr 2012 20:30:00",
	lastLocationBeaconTime:"27 Feb 2012 20:30:00",
	lastKnownLat:43.33434343,
	lastKnownLong:-5.32332343
}, {
	id:3,
	keyId:3,
	phoneNumber:"31648492710",
	deviceIdentifier:"Marks phone",
	deviceGroup:"WFP general",
	lastUpdate:"01 Sep 2012 20:30:00",
	lastLocationBeaconTime:"12 Aug 2012 20:30:00",
	lastKnownLat:34.222334234,
	lastKnownLong:-7.44343434
}, {
	id:4,
	keyId:4,
	phoneNumber:"34029392833",
	deviceIdentifier:"WFP colombia-1",
	deviceGroup:"Colombia",
	lastUpdate:"21 Aug 2012 20:30:00",
	lastLocationBeaconTime:"04 Jan 2012 20:30:00",
	lastKnownLat:2.334343434,
	lastKnownLong:-23.33433432
}, {
	id:5,
	keyId:5,
	phoneNumber:"3f:d4:8f:8b:8c:3e",
	deviceIdentifier:"WFP colombia 2",
	deviceGroup:"Colombia",
	lastUpdate:"12 Apr 2012 20:30:00",
	lastLocationBeaconTime:"31 Oct 2012 20:30:00",
	lastKnownLat:8.55454435,
	lastKnownLong:54.88399473
}, {
	id:6,
	keyId:6,
	phoneNumber:"2a:8c:9f:3f:d4:8f",
	deviceIdentifier:"WFP phone 3",
	deviceGroup:"Malawi",
	lastUpdate:"17 Jul 2012 20:30:00",
	lastLocationBeaconTime:"16 Jun 2012 20:30:00",
	lastKnownLat:23.988332,
	lastKnownLong:-64.88399483
}, {
	id:7,
	keyId:7,
	phoneNumber:"3403928293",
	deviceIdentifier:"WFP phone 4",
	deviceGroup:"Malawi",
	lastUpdate:"11 Dec 2012 20:30:00",
	lastLocationBeaconTime:"14 Nov 2012 20:30:00",
	lastKnownLat:23.3323432,
	lastKnownLong:9.88873633
}];

FLOW.SurveyedLocale.FIXTURES = [{
  keyId: 1,
  latitude: 52.370216,
  longitude: 4.895168,
  description: "Welkom in Amsterdam!",
  typeMark: "WATER_POINT"
}, {
  keyId: 2,
  latitude: 51.507335,
  longitude: -0.127683,
  description: "Welcome to London!",
  typeMark: "WATER_POINT"
}, {
  keyId: 3,
  latitude: 59.32893,
  longitude: 18.06491,
  description: "Välkommen till Stockholm!",
  typeMark: "WATER_POINT"
}];
