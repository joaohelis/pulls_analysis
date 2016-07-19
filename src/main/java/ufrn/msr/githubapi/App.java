/**
 * 
 */
package ufrn.msr.githubapi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.github.GHRateLimit;

import ufrn.msr.githubapi.dao.RepositoryDAO;
import ufrn.msr.githubapi.dao.hibernate.HibernateRepositoryDAO;
import ufrn.msr.githubapi.miner.GitLocalAndRemoteMiner;
import ufrn.msr.githubapi.models.Repository;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */
public class App {
	
	public static void main(String[] args) {
		
		Map<String, GHRateLimit> tokens = new HashMap<String, GHRateLimit>();
		tokens.put("PUT YOUR OAUTHTOKEN HERE", null);
		tokens.put("PUT YOUR SECOND OAUTHTOKEN HERE", null);
		
		List<String> repoNames = Arrays.asList(new String[]{
				"rails/rails",
				"elastic/elasticsearch", 
				"jekyll/jekyll", 
				"rg3/youtube-dl",
				"scrapy/scrapy", 
				"mitchellh/vagrant", 
				"scikit-learn/scikit-learn", 
				"ipython/ipython", 
				"capistrano/capistrano", 
				"thoughtbot/paperclip", 
				"spree/spree",
				"resque/resque",
				"fabric/fabric",
				"jnicklas/capybara",
				"netty/netty",
				"elastic/logstash",
				"boto/boto",
				"middleman/middleman",
				"cucumber/cucumber-ruby",
				"celery/celery",
				"thoughtbot/factory_girl",
				"dropwizard/dropwizard",
				"chef/chef",
				"divio/django-cms",
				"fluent/fluentd",
				"norman/friendly_id",
				"flyerhzm/bullet",
				"dropwizard/metrics",
				"airblade/paper_trail",
				"alexreisner/geocoder",
				"slim-template/slim",
				"sparklemotion/nokogiri",
				"binarylogic/authlogic",
				"fog/fog",
				"bottlepy/bottle",
				"sferik/twitter",
				"gradle/gradle",
				"bundler/bundler",
				"activemerchant/active_merchant",
				"refinery/refinerycms",
				"opal/opal",
				"railsbp/rails_best_practices",
				"benoitc/gunicorn",
				"vcr/vcr",
				"haml/haml",
				"mopidy/mopidy",
				"sympy/sympy",
				"rack/rack",
				"django-extensions/django-extensions",
				"jeremyevans/sequel",
				"paramiko/paramiko",
				"rubinius/rubinius",
				"apotonick/cells",
				"jruby/jruby",
				"mikel/mail",
				"aasm/aasm",
				"scipy/scipy",
				"thoughtbot/clearance",
				"octokit/octokit.rb",
				"buildbot/buildbot",
				"grosser/parallel",
				"wvanbergen/request-log-analyzer",
				"troessner/reek",
				"ruboto/ruboto",
				"markevans/dragonfly",
				"grails/grails-core",
				"grosser/parallel_tests",
				"mongodb/mongo-python-driver",
				"scambra/devise_invitable",
				"dennisreimann/ioctocat",
				"cython/cython",
				"mongomapper/mongomapper",
				"publify/publify"
		});
		
		RepositoryDAO repoDAO = new HibernateRepositoryDAO();
		
		for(String repoName: repoNames){
			Repository repo = repoDAO.getRepositoryByName(repoName);
			GitLocalAndRemoteMiner.releaseInformationMiner(repo);
			GitLocalAndRemoteMiner.pullRequestsInformationMiner(repo, tokens);
		}
		System.exit(0);		
	}
}
