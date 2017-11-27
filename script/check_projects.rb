require_relative 'csv.rb'

projects = CSV.parse(File.open("jmh-projects-bigquery-fh-201702 - jmh-projects-gh.csv"), header = true, separator = ",")
results = CSV.parse(File.open("detected_bugs_3.csv"), header = true, separator = ";" )

top25 = projects['project'][0..24].map{ |p| p.split("/")[-1] }
results_projects = results['project'].uniq

puts "Top-25 projects that we don't have results for yet:"
(top25 - results_projects).each{ |p| puts p }
