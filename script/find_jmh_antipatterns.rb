require_relative 'csv.rb'
require 'optparse'
require 'nokogiri'
require 'find'
require 'pry'

class JMHSpotBugsAutomater

  GH_URL_PATTERN = "https://github.com/PROJECT.git"
  GIT_CMD = "git"
  MVN_CMD = "/Users/philipp/apache-maven-3.3.9/bin/mvn"
  SPOTBUGS_CMD = "java -jar /Users/philipp/spotbugs-3.1.0/lib/spotbugs.jar -textui -bugCategories JMH -xml -output "
  TMP_XML_FILE = "tmp.xml"
  JMH_JAR_PATTERNS = ["jmh", "benchmark", "perf", "bench"]

  def initialize(outfile, tmp_dir, debug,  no_build = false)
    @debug = debug
    @tmp_dir = tmp_dir
    @outfile = outfile
    @skipbuild = no_build
    @result = CSV.new
    @result.headers = ["project", "bugtype", "count"]
  end

  def analyze_project(project)

    if !@skipbuild
      success, dir = clone_project(project['project'], @tmp_dir)
      @debug.puts "Failed to checkout project #{project['project']}" if !success
      resp_code, backend = try_compile dir
      @debug.puts "Failed to compile project #{project['project']} in #{dir}. Error code was #{resp_code} and detected backend was #{backend}." if resp_code != 0
      # this is a sanity check
      found = check_for_jmh_artefact dir
      @debug.puts "No JMHy artefact found  for #{project['project']} in #{dir}." if !found
    else
      project_name = project['project'].split("/")[1]
      dir = "#{@tmp_dir}/#{project_name}"
    end

    r = run_jmh_analysis(dir)
    if r != :failed
      r.each{|line| @result << line }
      @debug.puts "Successfully analyzed project #{project['project']}."
      @result.save(@outfile)
      @debug.puts "Successfully saved file"
    else
      @debug.puts "Failed to analyzed project #{project['project']}."
    end

    @debug.flush

  end

  ### start private methods ###
  private
  ### ###
    def clone_project(project, tmp_dir)

      gh_url = GH_URL_PATTERN
      url = gh_url.sub("PROJECT", project)
      project_name = project.split("/")[1]
      Dir.chdir(tmp_dir) do
        success = false
        if !Dir.exists? project_name
          success = system "#{GIT_CMD} clone #{url}"
        end
        return success, "#{tmp_dir}/#{project_name}"
      end
    end

    def try_compile(dir)
      Dir.chdir(dir) do
        if mvn?
          return compile_mvn, :mvn
        elsif gradlew?
          return compile_gradle, :gradlew
        else
          return -1, :none
        end
      end
    end

    def mvn?
      Dir["pom.xml"].size > 0
    end

    def gradlew?
      Dir["./gradlew"].size > 0
    end

    def compile_mvn
      system "#{MVN_CMD} install -DskipTests"
      $?
    end

    def compile_gradle
      system "./gradlew build -x test"
      $?
    end

    def check_for_jmh_artefact(dir)
      found = false
      JMH_JAR_PATTERNS.each do |pattern|
        Find.find(dir) do |file|
          if file =~ /.*#{pattern}.*\.jar$/
            found = true
          end
        end
      end
      return found
    end

    def run_jmh_analysis(dir)
      project = dir.split("/")[-1]
      success = system "#{SPOTBUGS_CMD} #{TMP_XML_FILE} #{dir}"
      if !success
        :failed
      else
        parse_spotbugs_xml project
      end
    end

    def parse_spotbugs_xml(project)
      doc = File.open(TMP_XML_FILE) { |f| Nokogiri::XML(f) }
      bugs = {}
      doc.xpath("//BugInstance").each do |bug|
        if bugs.key? bug[:type]
          bugs[bug[:type]] += 1
        else
          bugs[bug[:type]] = 1
        end
      end
      File.delete(TMP_XML_FILE)
      transpose_to_csv(bugs, project)
    end

end


def parse_cmd
  options = {}
  options[:no_build] = false
  OptionParser.new do |opts|

    opts.on("-i", "--input INPUTFILE", "Specify CSV file with list of projects") do |i|
      options[:input_file] = i
    end

    opts.on("-o", "--output OUTPUTFILE", "Specify file to write results to") do |o|
      options[:output_file] = o
    end

    opts.on("-t", "--tmp TMPDIR", "What tmp dir should we use to clone projects to?") do |t|
      options[:tmp_dir] = t || "tmp"
    end

    opts.on("-d", "--debug DEBUGFILE", "What file should we write debug info to?") do |d|
      options[:debug] = d || "debug.txt"
    end

    opts.on("-nb", "--no-build", "Set this flag to skip cloning and building") do |nb|
      options[:no_build] = true
    end

  end.parse!
  return options
end

def transpose_to_csv(buglist, project)
  # format: project;bugtype;count
  buglist.map{|bugtype,count| [project, bugtype, count] }
end

options = parse_cmd

File.open(options[:debug], "w") do |debug|

  f = File.open(options[:input_file])
  if !Dir.exists?(options[:tmp_dir])
    Dir.mkdir options[:tmp_dir]
  end

  jmh = JMHSpotBugsAutomater.new(options[:output_file], options[:tmp_dir], debug, options[:no_build])
  csv = CSV.parse(f, header = true, separator = ",")
  projects_to_consider = csv.reject{|l| l['forked'] == "TRUE" || l['stars'].to_i < 2 }
  projects_to_consider.each{|project| jmh.analyze_project(project) }
end
