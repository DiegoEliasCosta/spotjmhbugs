require 'pry'

class CSV
  include Enumerable

  attr_accessor :headers
  attr_accessor :lines
  attr_accessor :separator

  def initialize
    @headers = []
    @lines = []
    @separator = ";"
  end

  def self.parse(io, header = false, separator = ";")
      csv = CSV.new
      first_row = true
      csv.separator = separator
      io.each do |csv_line|
        csv_line.chomp!
        if first_row and header
          csv.headers = csv_line.split(separator)
          first_row = false
        else
          content = csv_line.split(separator)
          if content.size != csv.columns
            raise "Parse error: incompatible column count in line "+csv_line
          end
          line = CSVLine.new
          line.headers = csv.headers if csv.headers
          line.content = content
          csv.lines << line
        end
      end
      return csv
  end

  def save(file)
    File.open(file, "w") do |f|
      write_line(f, @headers)
      @lines.each do |line|
        write_line(f, line.content)
      end
    end
  end

  def size
    @lines.size
  end

  def columns
    if @headers.size > 0
      @headers.size
    elsif @lines.size > 0
      @lines[0].size
    else
      0
    end
  end

  def each(&block)
    @lines.each(&block)
  end

  def [](column)
    if column.is_a? Integer
      return @lines[column]
    else
      unless @headers.include? column
        raise "Unknown column "+column
      end
      idx = @headers.index(column)
      return @lines.map{|line| line[idx] }
    end
  end

  def <<(line)

    l = CSVLine.new
    if @headers
      if @headers.size != line.size
        raise "Incompatible column numbers for line #{line} and headers #{@headers}"
      else
        l.headers = @headers
      end
    end
    l.content = line
    @lines << l

  end

  def -(line)
    @lines -= [line]
  end

  def to_s
    s = @headers.to_s unless @headers.size == 0
    s << "\n<<#{size} lines of content>>"
    return s
  end

  private

  def write_line(f, entries)
    buffer = ""
    entries.each{ |it| buffer += it.to_s + separator }
    buffer.chomp! separator
    f.puts buffer
  end
end

class CSVLine

  attr_accessor :headers
  attr_accessor :content

  def initialize
    @headers = []
    @content = []
  end

  def each(&block)
    @content.each(&block)
  end

  def [](column)
    if column.is_a? Integer
      return @content[column]
    else
      unless @headers.include? column
        raise "Unknown column "+column
      end
      return @content[@headers.index(column)]
    end
  end

  def []=(column, value)
    if column.is_a? Integer
      @content[column] = value
    else
      unless @headers.include? column
        raise "Unknown column "+column
      end
      @content[@headers.index(column)] = value
    end
  end

  def to_s
    s = @headers.to_s unless @headers.size == 0
    s << @content.to_s
    return s
  end

end


# file = File.new("/Users/philipp/git_repos/projects/hopper/ptc/results/rq2/Java/prestudy/log4j2_aggregated.csv")
# csv = CSV.parse(file, true)
# counts = {}
# csv.each do |_,_,method,count|
#   unless counts.key? method
#     counts[method] = 0
#   end
#   counts[method] += count.to_i
# end
# counts.sort{|e1, e2| e2[1] <=> e1[1] }.each do |pair|
#   puts "%s -> %d" % pair
# end
