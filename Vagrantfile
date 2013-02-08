# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant::Config.run do |config|
  # All Vagrant configuration is done here. The most common configuration
  # options are documented and commented below. For a complete reference,
  # please see the online documentation at vagrantup.com.

  config.vm.host_name = "akvo-flow.dev"
  config.vm.box = "akvo-flow_precise64_4.2.6_v3"

  config.vm.boot_mode = :gui

  config.vm.network :hostonly, "33.33.33.6"
  config.vm.share_folder("v-root", "/akvo-flow", ".", :nfs => true)


  config.vm.provision :puppet do |puppet|
    puppet.manifests_path = "dev-env/puppet/manifests"
    puppet.manifest_file = "init.pp"
    puppet.module_path = "dev-env/puppet/modules"
    # puppet.options = "--verbose --debug"
    puppet.options = "--verbose"
  end

end
