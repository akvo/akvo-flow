/* eslint-disable import/prefer-default-export */

export function ArrNoDupe(a) {
  let gotIt;
  const templ = {};
  const tempa = Ember.A([]);

  for (let i = 0; i < a.length; i++) {
    templ[a.objectAt(i).clientId] = true;
  }

  const keys = Object.keys(templ);
  for (let j = 0; j < keys.length; j++) {
    gotIt = false;
    for (let i = 0; i < a.length; i++) {
      if (a.objectAt(i).clientId == keys[j] && !gotIt) {
        tempa.pushObject(a.objectAt(i));
        gotIt = true;
      }
    }
  }
  return tempa;
}
