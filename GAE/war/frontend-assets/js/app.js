
// Create the application
window.FLOW = Ember.Application.create();


// ***********************************************//
//                 Initialize on pageload                    
// ***********************************************//

$(function(){
	FLOW.router = FLOW.Router.create()
	FLOW.initialize(FLOW.router);
});


