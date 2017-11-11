require 'pry'
require 'find'

class JMH

  JMH_BENCHMARK_MARKER = /@Benchmark/

  def initialize(dir)
    @dir = dir
  end

  def count_benchmarks
    exec_in_dir do
      counts = {}
      files = all_benchmark_files
      files.each do |file|
        counts[file] = count_benchmarks_in_file file
      end
      return counts
    end
  end

  def count_benchmarks_in_file(file)
    File.open(file).grep(JMH_BENCHMARK_MARKER).size
  end

  ### start private methods ###
  private
  ### ###
  def exec_in_dir
    Dir.chdir(@dir) do
      yield
    end
  end

  def all_benchmark_files
    files = []
    Find.find(".") do |file|
      files << file if (file =~ /.+\.java$/ && File.open(file).grep(JMH_BENCHMARK_MARKER).size > 0)
    end
    return files
  end

end
