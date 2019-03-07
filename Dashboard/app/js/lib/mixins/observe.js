export default mappings => Ember.Mixin.create({
  init() {
    this._super();
    Object.keys(mappings).forEach((key) => {
      this.addObserver(key, this, mappings[key]);
    });
  },
});
