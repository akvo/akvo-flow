// ***********************************************//
//                 models and stores
// ***********************************************//
require('akvo-flow/core');
DS.Model.reopen({
	namingConvention: {
		keyToJSONKey: function(key) {
			return key;
		},

		foreignKey: function(key) {
			return key;
		}
	}
});

FLOW.store = DS.Store.create({
	revision: 4,
	//adapter:DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"REST"})//change
	adapter: DS.fixtureAdapter
});

FLOW.Device = DS.Model.extend({
	deviceType: DS.attr('string',{defaulValue: ""}),
	phoneNumber: DS.attr('string',{defaulValue: ""}),
	esn: DS.attr('string',{defaulValue: ""}),
	deviceIdentifier: DS.attr('string',{defaulValue: ""}),
	inServiceDate: DS.attr('date'),
	outServiceDate: DS.attr('date'),
	lastUpdate: DS.attr('string',{defaulValue: ""}),	//should be DS.attr('date'),
	osVersion: DS.attr('string',{defaulValue: ""}),
	lastKnownLat: DS.attr('number',{defaulValue: 0}),
	lastKnownLong:DS.attr('number',{defaulValue: 0}),
	lastKnownAccuracy: DS.attr('number',{defaulValue:0}),
	lastLocationBeaconTime: DS.attr('string',{defaulValue: ""}), //should be DS.attr('date'),
	deviceGroup: DS.attr('string',{defaulValue: ""}),
	keyId: DS.attr('number'),
	primaryKey:'keyId'
});


FLOW.Device.FIXTURES = [
{
	keyId:1,
	phoneNumber:"3f:d4:8f:2a:8c:9f",
	deviceIdentifier:"Keri phone 1",
	deviceGroup:"WFP general",
	lastUpdate:"21 May 2012 20:30:00",
	lastLocationBeaconTime:"22 May 2012 20:30:00",
	lastKnownLat:23.132132321,
	lastKnownLong:12.23232332
},
{
	keyId:2,
	phoneNumber:"2a:8c:9f:3f:d4:8f",
	deviceIdentifier:" Keri phone 2",
	deviceGroup:"WFP general",
	lastUpdate:"21 Apr 2012 20:30:00",
	lastLocationBeaconTime:"27 Feb 2012 20:30:00",
	lastKnownLat:43.33434343,
	lastKnownLong:-5.32332343
},
{
	keyId:3,
	phoneNumber:"31648492710",
	deviceIdentifier:"Marks phone",
	deviceGroup:"WFP general",
	lastUpdate:"01 Sep 2012 20:30:00",
	lastLocationBeaconTime:"12 Aug 2012 20:30:00",
	lastKnownLat:34.222334234,
	lastKnownLong:-7.44343434
},
{
	keyId:4,
	phoneNumber:"34029392833",
	deviceIdentifier:"WFP colombia-1",
	deviceGroup:"Colombia",
	lastUpdate:"21 Aug 2012 20:30:00",
	lastLocationBeaconTime:"04 Jan 2012 20:30:00",
	lastKnownLat:2.334343434,
	lastKnownLong:-23.33433432
},
{
	keyId:5,
	phoneNumber:"3f:d4:8f:8b:8c:3e",
	deviceIdentifier:"WFP colombia 2",
	deviceGroup:"Colombia",
	lastUpdate:"12 Apr 2012 20:30:00",
	lastLocationBeaconTime:"31 Oct 2012 20:30:00",
	lastKnownLat:8.55454435,
	lastKnownLong:54.88399473
},
{
	keyId:6,
	phoneNumber:"2a:8c:9f:3f:d4:8f",
	deviceIdentifier:"WFP phone 3",
	deviceGroup:"Malawi",
	lastUpdate:"17 Jul 2012 20:30:00",
	lastLocationBeaconTime:"16 Jun 2012 20:30:00",
	lastKnownLat:23.988332,
	lastKnownLong:-64.88399483
},
{
	keyId:7,
	phoneNumber:"3403928293",
	deviceIdentifier:"WFP phone 4",
	deviceGroup:"Malawi",
	lastUpdate:"11 Dec 2012 20:30:00",
	lastLocationBeaconTime:"14 Nov 2012 20:30:00",
	lastKnownLat:23.3323432,
	lastKnownLong:9.88873633
}]


FLOW.SurveyGroup = DS.Model.extend({
	surveyList: DS.attr('string',{defaulValue: ""}),
	description: DS.attr('string',{defaulValue: ""}),
	name: DS.attr('string',{defaulValue: ""}),
	createdDateTime: DS.attr('string',{defaulValue: ""}),
	lastUpdateDateTime: DS.attr('string',{defaulValue: ""}),
	code: DS.attr('string',{defaulValue: ""}),
	displayName: DS.attr('string',{defaulValue: ""}),
	keyId: DS.attr('number'),
	primaryKey: 'keyId'

});


FLOW.SurveyGroup.FIXTURES = [{
	keyId: 1,
	displayName: 'Urban sanitation surveys'
}, {
	keyId: 2,
	displayName: 'Elise Surveys'
}, {
	keyId: 3,
	displayName: 'Test Survey group'
},{
	keyId: 4,
	displayName: 'Upande - SNVVERMIS'
},{
	keyId: 5,
	displayName: 'Akvo test surveys'
}

];


FLOW.Survey = DS.Model.extend({
	defaultLanguageCode: DS.attr('string'),
	status: DS.attr('string'),
	code: DS.attr('string'),
	requireApproval: DS.attr('string'),
	version: DS.attr('string'),
	description: DS.attr('string'),
	name: DS.attr('string'),
	path: DS.attr('string'),
	displayName: DS.attr('string'),
	questionGroupList: DS.attr('string'),
	keyId: DS.attr('number'),
	pointType: DS.attr('string'),
	surveyGroupId: DS.attr('number'),
	primaryKey: 'keyId'

});

FLOW.Survey.FIXTURES = [{
	keyId: 1,
	displayName: 'Water point survey',
	name:'Water point survey',
	surveyGroupId: 1
}, {
	keyId: 2,
	displayName: 'Sanitation survey',
	name:'Sanitation survey',
	surveyGroupId: 1
}, {
	keyId: 3,
	displayName: 'Baseline WASH',
	name:'Baseline WASH',
	surveyGroupId: 1
}, {
	keyId: 4,
	displayName: 'Akvo RSR update',
	name:'Akvo RSR update',
	surveyGroupId: 1
},
{
	keyId: 5,
	displayName: 'Akvo update',
	name:'Akvo update',
	surveyGroupId: 1
},
{
	keyId: 6,
	displayName: 'Loics survey',
	name:'Loics survey',
	surveyGroupId: 1
},
{
	keyId: 7,
	displayName: 'Farmer survey',
	name:'Farmer survey',
	surveyGroupId: 1
},
{
	keyId: 8,
	displayName: 'Rabbit',
	name:'Rabbit',
	surveyGroupId: 1
},
{
	keyId: 9,
	displayName: 'Rabbit II',
	name:'Rabbit II',
	surveyGroupId: 1
}];



FLOW.QuestionGroup = DS.Model.extend({
	order: DS.attr('number'),
	questionMap: DS.attr('string'),
	description: DS.attr('string'),
	name: DS.attr('string'),
	path: DS.attr('string'),
	class: DS.attr('string'),
	code: DS.attr('string'),
	surveyId: DS.attr('number'),
	displayName: DS.attr('string'),
	keyId: DS.attr('number'),
	isOpen: DS.attr('boolean', {
		defaulValue: 0
	}),
	primaryKey: 'keyId'
});


FLOW.QuestionGroup.FIXTURES = [{
	keyId: 1,
	surveyId:1,
	order:1,
	description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu. ",
	name: 'Location',
	displayName: 'Location'

}, {
	keyId: 2,
	surveyId:1,
	order:2,
	description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
	name: 'Occupation',
	displayName: 'Occupation'

}, {
	keyId: 3,
	surveyId:1,
	order:3,
	description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
	name: 'Water system',
	displayName: 'Water system'

}, {
	keyId: 4,
	surveyId:1,
	order:4,
	description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
	name: 'Sanitation system',
	displayName: 'Sanitation system'

},
{
	keyId: 5,
	surveyId:2,
	order:5,
	description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
	name: 'Something else',
	displayName: 'Something else'

}

];



FLOW.Question = DS.Model.extend({
	translationMap: DS.attr('string'),
	// not sure
	allowDecimal: DS.attr('boolean', {defaulValue: 0}),
	optionContainerDto: DS.attr('string'),
	//not sure
	// type:     --- not now
	tip: DS.attr('string'),
	maxVal: DS.attr('number'),
	minVal: DS.attr('number'),
	order: DS.attr('number'),
	isName: DS.attr('boolean', {defaulValue: 0}),
	// questionHelpList:     --- now now
	collabseable: DS.attr('boolean', {defaulValue: 0}),
	path: DS.attr('string'),
	allowMultipleFlag: DS.attr('boolean', {defaulValue: 0}),
	immutable: DS.attr('boolean', {defaulValue: 0}),
	allowOtherFlag: DS.attr('boolean', {defaulValue: 0}),
	allowSign: DS.attr('boolean', {defaulValue: 0}),
	text: DS.attr('string'),
	class: DS.attr('string'),
	questionDependency: DS.attr('string'),
	mandatoryFlag: DS.attr('boolean', {defaulValue: 0}),
	questionGroupId: DS.attr('number'),
	questionTypeString: DS.attr('string',{defaultValue:"freeText"}),
	surveyId: DS.attr('number'),
	displayName: DS.attr('string'),
	keyId: DS.attr('number')
});


FLOW.Question.FIXTURES = [{
	keyId: 1,
	text: 'What is the name of the community?',
	displayName: 'What is the name of the community?',
	mandatory: false,
	order:1,
	questionTypeString:'freeText',
	questionSetId: 1
}, {
	keyId: 2,
	text: 'What is your occupation?',
	displayName: 'What is your occupation?',
	mandatory: false,
	order:2,
	questionTypeString:'option',
	questionSetId: 1
}, {
	keyId: 3,
	text: 'How much children do you have?',
	displayName: 'How much children do you have?',
	mandatory: false,
	order:3,
	questionTypeString:'number',
	questionSetId: 1
}, {
	keyId: 4,
	text: 'Please take a geolocation',
	displayName: 'Please take a geolocation',
	mandatory: false,
	order:4,
	questionTypeString:'geoLoc',
	questionSetId: 1
}, 	{
	keyId: 4,
	text: 'Please take a picture',
	displayName: 'Please take a picture',
	mandatory: false,
	order:5,
	questionTypeString:'photo',
	questionSetId: 1
},	{
	keyId: 4,
	text: 'Please make a video',
	displayName: 'Please make a video',
	questionTypeString:'video',
	order:6,
	mandatory: false,
	questionSetId: 1
},	{
	keyId: 4,
	text: 'What is the date today?',
	displayName: 'What is the date today?',
	questionTypeString:'date',
	order:7,
	mandatory: false,
	questionSetId: 1
}];

FLOW.QuestionOption = DS.Model.extend({
	questionId: DS.attr('number'),
	text: DS.attr('string'),
	keyId: DS.attr('number')
});

FLOW.QuestionOption.FIXTURES = [{
	keyId: 1,
	text: 'teacher',
	questionId: 1
}, {
	keyId: 2,
	text: 'cook',
	questionId: 1
},{
	keyId: 3,
	text: 'minister',
	questionId: 1
},{
	keyId: 4,
	text: 'programmer',
	questionId: 1
}];

FLOW.Location = DS.Model.extend({
  keyId:      DS.attr("number"),
  label:      DS.attr("string"),
  latitude:   DS.attr("number"),
  longitude:  DS.attr("number")
});

FLOW.Location.FIXTURES = [{
  keyId:      1,
  label:      "Edinburgh",
  latitude:   55.953252,
  longitude:  -3.188267
},{
  keyId:      2,
  label:      "Reykjavík",
  latitude:   64.135338,
  longitude:  -21.89521
}];
