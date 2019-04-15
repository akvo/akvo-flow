const sass = require('node-sass');
const path = require('path');
const fs = require('fs-extra');

const targetDir = path.join(__dirname, '../../GAE/target/akvo-flow/admin/css/');

fs.ensureDirSync(targetDir);

sass.render({
  file: path.join(__dirname, '../app/css/users.scss'),
  outputStyle: 'compressed',
}, (err, result) => {
  if (err) {
    throw err;
  }
  fs.writeFileSync(path.join(targetDir, './users.min.css'), result.css);
  console.log('Successfully compiled users css...');
});
