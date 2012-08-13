// A helper function to define a property used to render the navigation. Returns
// true if a state with the specified name is somewhere along the current route. 
// Create the application
window.FLOW = Ember.Application.create();


// ***********************************************//
//                 Initialize on pageload                    
// ***********************************************//

$(function(){
	FLOW.router = FLOW.Router.create()
	FLOW.initialize(FLOW.router);
});


