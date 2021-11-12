import ReactDOM from 'react-dom';

FLOW.ReactComponentView = Ember.View.extend({
  template: Ember.Handlebars.compile(''),

  reactRender(reactComponent) {
    const elements = this.get('element');
    if (elements) {
      ReactDOM.render(reactComponent, elements);
    } else {
      console.warn('React mount element not found');
    }
  },

  unmountReactElement() {
    ReactDOM.unmountComponentAtNode(this.get('element'));
  },

  willDestroyElement() {
    this._super();
    this.unmountReactElement();
  },
});
